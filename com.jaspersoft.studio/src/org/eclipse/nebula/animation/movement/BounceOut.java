/*
 * Port of Robert Penner's easing equations for Nebula animation package.
 * Copyright (c) 2008-2009 Nicolas Richeton.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
/*
 * TERMS OF USE - EASING EQUATIONS
 * 
 * Open source under the BSD License.
 * 
 * Copyright (c) 2001 Robert Penner All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the author nor the names of
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package org.eclipse.nebula.animation.movement;
/*
 * Bounces at the end like a falling ball on the floor.
 * 
 * @author Nicolas Richeton
 * 
 */
public class BounceOut extends AbstractMovement {

	public double getValue(double step) {
		// Conversion from Robert Penner's action scripts
		//
		// t : time -> step
		// b: min -> min
		// c : total increment -> max - min
		// d: duration -> duration

		double c = max - min;
		step = step / duration;

		if (step == 1)
			return max;

		if (step < (1 / 2.75)) {

			return c * (7.5625 * step * step) + min;

		} else if (step < (2 / 2.75)) {

			return c * (7.5625 * (step -= (1.5 / 2.75)) * step + .75) + min;

		} else if (step < (2.5 / 2.75)) {

			return c * (7.5625 * (step -= (2.25 / 2.75)) * step + .9375) + min;

		} else {

			return c * (7.5625 * (step -= (2.625 / 2.75)) * step + .984375)
					+ min;
		}

	}

}
