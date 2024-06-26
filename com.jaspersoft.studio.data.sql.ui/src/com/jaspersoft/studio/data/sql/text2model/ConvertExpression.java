/*******************************************************************************
 * Copyright © 2010-2023. Cloud Software Group, Inc. All rights reserved.
 *******************************************************************************/
package com.jaspersoft.studio.data.sql.text2model;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.jaspersoft.studio.data.sql.ColumnFull;
import com.jaspersoft.studio.data.sql.Comparison;
import com.jaspersoft.studio.data.sql.FullExpression;
import com.jaspersoft.studio.data.sql.JRParameter;
import com.jaspersoft.studio.data.sql.Like;
import com.jaspersoft.studio.data.sql.LikeOperand;
import com.jaspersoft.studio.data.sql.OpFunction;
import com.jaspersoft.studio.data.sql.OpFunctionArg;
import com.jaspersoft.studio.data.sql.Operand;
import com.jaspersoft.studio.data.sql.Operands;
import com.jaspersoft.studio.data.sql.OrExpr;
import com.jaspersoft.studio.data.sql.QueryWriter;
import com.jaspersoft.studio.data.sql.SQLQueryDesigner;
import com.jaspersoft.studio.data.sql.Util;
import com.jaspersoft.studio.data.sql.XExpr;
import com.jaspersoft.studio.data.sql.impl.DbObjectNameImpl;
import com.jaspersoft.studio.data.sql.impl.OperandImpl;
import com.jaspersoft.studio.data.sql.impl.ScalarOperandImpl;
import com.jaspersoft.studio.data.sql.impl.SelectImpl;
import com.jaspersoft.studio.data.sql.model.enums.Operator;
import com.jaspersoft.studio.data.sql.model.metadata.MSQLColumn;
import com.jaspersoft.studio.data.sql.model.query.expression.AMExpression;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpression;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionGroup;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionPNot;
import com.jaspersoft.studio.data.sql.model.query.expression.MExpressionX;
import com.jaspersoft.studio.data.sql.model.query.from.MFromTable;
import com.jaspersoft.studio.data.sql.model.query.operand.AOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FieldOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.FunctionOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ParameterNotPOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ParameterPOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.ScalarOperand;
import com.jaspersoft.studio.data.sql.model.query.operand.UnknownOperand;
import com.jaspersoft.studio.data.sql.model.query.select.MSelect;
import com.jaspersoft.studio.data.sql.model.query.select.MSelectSubQuery;
import com.jaspersoft.studio.model.ANode;

import net.sf.jasperreports.eclipse.util.KeyValue;
import net.sf.jasperreports.eclipse.util.Misc;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignParameter;

public class ConvertExpression {

	public static String convertExpression2String(SQLQueryDesigner designer, ANode qroot, ANode parent, OrExpr cols) {
		convertExpression(designer, qroot, parent, cols);
		return QueryWriter.writeSubQuery(parent).replaceFirst("\n", "");
	}

	public static void convertExpression(SQLQueryDesigner designer, ANode qroot, ANode parent, OrExpr cols) {
		try {
			if (cols == null)
				return;
			if (cols instanceof FullExpression)
				doExpression(designer, Util.getKeyword(qroot, MSelect.class), qroot, parent, (FullExpression) cols);
			else if (cols instanceof OrExpr) {
				MSelect msel = Util.getKeyword(qroot, MSelect.class);
				for (FullExpression fcol : cols.getEntries())
					doExpression(designer, msel, qroot, parent, fcol);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void doExpression(SQLQueryDesigner designer, MSelect msel, ANode qroot, ANode parent,
			FullExpression tf) {
		String notPrm = tf.getNotPrm();
		if (notPrm != null) {
			JRDesignDataset jrDataset = msel.getRoot().getValue();
			if (jrDataset == null)
				jrDataset = designer.getjDataset();
			JRDesignParameter prm = null;
			for (net.sf.jasperreports.engine.JRParameter p : jrDataset.getParametersList()) {
				if (p.getName().equals(notPrm))
					prm = (JRDesignParameter) p;
			}
			if (prm == null) {
				prm = new JRDesignParameter();
				prm.setName(notPrm.substring(4, notPrm.length() - 1));
			}
			new MExpressionPNot(parent, prm, -1);
			return;
		}
		String prevCond = tf.getC();
		if (tf.getEfrag() != null)
			tf = tf.getEfrag();
		if (tf.getExp() != null) {
			tf = tf.getExp();
			MExpression me = new MExpression(parent, null, -1);
			List<AOperand> opds = me.getOperands();
			opds.add(getOperand(designer, msel, tf.getOp1(), me));
			Comparison comp = tf.getComp();
			if (comp != null) {
				me.setOperator(Operator.getOperator(comp.getOperator()));
				opds.add(getOperand(designer, msel, comp.getOp2(), me));
			} else if (tf.getIsnull() != null)
				me.setOperator(Operator.getOperator(tf.getIsnull()));
			else if (tf.getBetween() != null) {
				me.setOperator(Operator.getOperator(tf.getBetween().getOpBetween()));
				opds.add(getOperand(designer, msel, tf.getBetween().getOp2(), me));
				opds.add(getOperand(designer, msel, tf.getBetween().getOp3(), me));
			} else {
				Like like = tf.getLike();
				if (like != null) {
					me.setOperator(Operator.getOperator(like.getOpLike()));
					LikeOperand op2 = like.getOp2();
					if (op2.getOp2() != null)
						opds.add(new ScalarOperand<String>(me, op2.getOp2().replaceAll("^'|'$", "")));
					else if (op2.getFop2() != null)
						opds.add(new UnknownOperand(me,
								ConvertSelectColumns.getFunctionString(designer, msel, parent, op2.getFop2(), msel)));
					else if (op2.getFcast() != null)
						opds.add(new UnknownOperand(me,
								ConvertSelectColumns.getFunctionString(designer, msel, parent, op2.getFcast(), msel)));
					else if (op2.getFparam() != null) {
						String p = op2.getFparam().getPrm();
						p = p.substring(3, p.length() - 1);

						addParam(me, p, designer.getjDataset());
					}
				} else if (tf.getIn() != null) {
					me.setOperator(Operator.getOperator(tf.getIn().getOp().replace("(", "").trim()));
					if (tf.getIn().getSubquery() != null) {
						MSelectSubQuery qroot1 = new MSelectSubQuery(me);
						Util.createSelect(qroot1);
						Text2Model.convertSelect(designer, qroot1, (SelectImpl) tf.getIn().getSubquery().getSel());
					} else if (tf.getIn().getOpList() != null) {
						if (tf.getIn().getOpList() instanceof ScalarOperandImpl) {
							opds.add(getScalarOperand(me, (ScalarOperandImpl) tf.getIn().getOpList()));
						} else
							for (EObject eobj : tf.getIn().getOpList().eContents()) {
								if (eobj instanceof Operand)
									opds.add(getOperand(designer, msel, (Operand) eobj, me));
							}
					}
				}
			}
			setPrevOperator(me, prevCond);
		} else if (tf.getExpgroup() != null) {
			MExpressionGroup meg = new MExpressionGroup(parent);
			if (tf.getExpgroup().getIsnot() != null)
				meg.setNot(true);
			convertExpression(designer, msel.getParent(), meg, tf.getExpgroup().getExpr());
		} else if (tf.getXexp() != null) {
			MExpressionX me = new MExpressionX(parent, null, -1);
			XExpr xexpr = tf.getXexp();
			AOperand fc = null;
			if (xexpr.getCol() != null)
				fc = getOperand(designer, msel, xexpr.getCol(), me);
			if (fc != null && fc instanceof FieldOperand)
				me.getOperands().add(fc);
			else if (xexpr.getCol() != null)
				me.getOperands().add(new UnknownOperand(me,
						ConvertSelectColumns.operands2String(designer, qroot, parent, xexpr.getCol(), msel)));
			else
				me.getOperands().add(new FieldOperand(null, null, me));
			me.setFunction(xexpr.getXf().getLiteral().replace("{", ""));
			if (xexpr.getPrm() != null) {
				JRDesignDataset jrDataset = designer.getjDataset();
				if (xexpr.getPrm() instanceof JRParameter)
					addParam(me, (JRParameter) xexpr.getPrm(), jrDataset);
				else
					for (EObject fcol : xexpr.getPrm().eContents()) {
						if (fcol instanceof JRParameter)
							addParam(me, (JRParameter) fcol, jrDataset);
					}
			}
			setPrevOperator(me, prevCond);
		}
	}

	protected static void addParam(MExpressionX me, JRParameter fcol, JRDesignDataset jrDataset) {
		ParameterPOperand pop = new ParameterPOperand(me);
		pop.setJrDataset(jrDataset);
		pop.setJrParameter(fcol.getJrprm());
		me.getOperands().add(pop);
	}

	protected static void addParam(MExpression me, String prm, JRDesignDataset jrDataset) {
		ParameterPOperand pop = new ParameterPOperand(me);
		pop.setJrDataset(jrDataset);
		pop.setJrParameter(prm);
		me.getOperands().add(pop);
	}

	private static void setPrevOperator(AMExpression<?> me, String prevCond) {
		if (prevCond != null)
			me.setPrevCond(prevCond.trim().toUpperCase());
	}

	private static AOperand getOperand(SQLQueryDesigner designer, MSelect msel, Operands ops, AMExpression<?> me) {
		if (ops == null)
			return null;
		if (ops instanceof OperandImpl)
			return getOperand(designer, msel, (Operand) ops, me);
		if (ops.getOp1() != null)
			return getOperand(designer, msel, ops.getOp1(), me);
		if (ops.getLeft() != null)
			return getOperand(designer, msel, ops.getLeft(), me);
		if (ops.getRight() != null)
			return getOperand(designer, msel, ops.getRight(), me);
		return null;
	}

	private static AOperand getOperand(SQLQueryDesigner designer, MSelect msel, Operand op, AMExpression<?> me) {
		AOperand aop = null;
		if (op.getColumn() != null) {
			aop = doColumn(designer, msel, op.getColumn().getCfull(), me);
		} else if (op.getScalar() != null) {
			aop = getScalarOperand(me, op.getScalar());
		} else if (op.getXop() != null) {
			Operand xop = op.getXop();
			if (xop.getScalar() != null) {
				aop = getScalarOperand(me, op.getXop().getScalar());
			} else if (xop.getParam() != null) {
				ParameterPOperand pop = new ParameterPOperand(me);
				pop.setJrParameter(ConvertUtil.getParamValue(xop.getParam().getPrm()));
				return pop;
			} else if (xop.getEparam() != null) {
				ParameterNotPOperand prm = new ParameterNotPOperand(me);
				prm.setJrParameter(ConvertUtil.getParamExclamationValue(xop.getEparam().getPrm()));
				return prm;
			}
		} else if (op.getSubq() != null) {

		} else if (op.getFunc() != null) {
			OpFunction f = op.getFunc();
			if (f.getStar() != null)
				aop = new FunctionOperand(me, f.getFname(), f.getStar());
			else if (f.getArgs() != null) {
				List<AOperand> args = new ArrayList<>();
				OpFunctionArg fargsImpl = f.getArgs();
				for (EObject eobj : fargsImpl.eContents()) {
					if (eobj instanceof Operands)
						args.add(getOperand(designer, msel, ((Operands) eobj), me));
					else if (eobj instanceof Operand)
						args.add(getOperand(designer, msel, (Operand) eobj, me));
				}
				aop = new FunctionOperand(me, f.getFname(), args);
			} else if (f.getFan() != null) {
				String v = "HERE ANALYTICAL CLAUSE";
				// f.getFan().getAnClause().get .
				aop = new FunctionOperand(me, f.getFname(), v);
			}
		}
		return Misc.nvl(aop, new ScalarOperand<String>(me, "NULL"));
	}

	protected static AOperand getScalarOperand(AMExpression<?> me, com.jaspersoft.studio.data.sql.ScalarOperand sop) {
		if (sop.getSostr() != null) {
			String str = sop.getSostr();
			str = str.substring(1, str.length() - 1);
			return new ScalarOperand<String>(me, str);
		}
		if (sop.getSodate() != null) {
			Date d;
			try {
				d = new SimpleDateFormat("YYYY-MM-dd").parse(sop.getSodate());
				return new ScalarOperand<java.sql.Date>(me, new java.sql.Date(d.getTime()));
			} catch (ParseException e) {
				return new ScalarOperand<String>(me, sop.getSodate());
			}
		}
		if (sop.getSodbl() != null)
			return new ScalarOperand<BigDecimal>(me, sop.getSodbl());
		if (sop.getSoUInt() != 0)
			return new ScalarOperand<Long>(me, sop.getSoUInt());
		if (sop.getSoint() != 0)
			return new ScalarOperand<Long>(me, sop.getSoint());
		if (sop.getSodt() != null) {
			Date d;
			try {
				d = new SimpleDateFormat("YYYY-mm-dd HH:mm:ss.SSSSSS").parse(sop.getSodt());
			} catch (ParseException e) {
				return new ScalarOperand<String>(me, sop.getSodt());
			}
			return new ScalarOperand<Timestamp>(me, new Timestamp(d.getTime()));
		}
		if (sop.getSotime() != null) {
			Date d;
			try {
				d = new SimpleDateFormat("HH:mm:ss.SSSSSS").parse(sop.getSotime());
			} catch (ParseException e) {
				return new ScalarOperand<String>(me, sop.getSotime());
			}
			return new ScalarOperand<Time>(me, new Time(d.getTime()));
		}
		return new ScalarOperand<BigDecimal>(me, sop.getSodbl());
	}

	private static FieldOperand doColumn(SQLQueryDesigner designer, MSelect msel, ColumnFull tf,
			AMExpression<?> mexpr) {
		EList<EObject> eContents = tf.eContents();
		String column = null;
		if (tf instanceof DbObjectNameImpl)
			column = ((DbObjectNameImpl) tf).getDbname();
		else
			column = ConvertUtil.getDbObjectName(eContents, 1);
		String table = ConvertUtil.getDbObjectName(eContents, 2);
		String schema = ConvertUtil.getDbObjectName(eContents, 3);
		KeyValue<MSQLColumn, MFromTable> kv = ConvertUtil.findColumn(msel, schema, table, column, designer);
		if (kv != null)
			return new FieldOperand(kv.key, kv.value, mexpr);
		return null;
	}

}
