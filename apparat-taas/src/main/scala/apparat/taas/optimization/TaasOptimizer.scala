/*
 * This file is part of Apparat.
 *
 * Copyright (C) 2010 Joa Ebert
 * http://www.joa-ebert.com/
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package apparat.taas.optimization

import annotation.tailrec
import apparat.taas.ast.{TaasMethod, TaasCode}
import apparat.utils.Performance
import apparat.log.SimpleLog

/**
 * @author Joa Ebert
 */
class TaasOptimizer(optimizations: List[TaasOptimization], level: Int) extends SimpleLog {
	val MAX_ITERATIONS = 0x20

	def optimize(method: TaasMethod): Unit = method.code match {
		case Some(code) => optimize(code)
		case None =>
	}

	def optimize(code: TaasCode): Unit = {
		@tailrec def next(optimizations: List[TaasOptimization], context: TaasOptimizationContext): TaasOptimizationContext = optimizations match {
			case x :: xs => next(xs, debug(x, context) { x optimize context } )
			case Nil => context
		}

		@tailrec def loop(context: TaasOptimizationContext, iteration: Int): Unit = {
			if(iteration < MAX_ITERATIONS) {
				next(optimizations, context) match {
					case newContext @ TaasOptimizationContext(_, true, _, _) => loop(newContext.copy(modified = false), iteration + 1)
					case _ =>
				}
			} else {
				error("Potential error detected: MAX_ITERATIONS reached.")
			}
		}

		loop(TaasOptimizationContext(code, false, level, TaasOptimizationFlags.NONE), 0)
	}

	def debug(optimizer: TaasOptimization, context: TaasOptimizationContext)(f: => TaasOptimizationContext) = {
		if(0 != (context.flags & TaasOptimizationFlags.DEBUG)) {
			Performance.measure(optimizer.name+" time") {
				val modified = context.modified
				val result = f
				log.debug(optimizer.name+" transformation: "+modified+" -> "+result.modified)
				result
			}
		} else {
			f
		}
	}
}
