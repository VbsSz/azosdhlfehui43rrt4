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
package apparat.log

/**
 * @author Joa Ebert
 */
sealed abstract class LogLevel(val priority: Int) {
	def >=(that: Int): Boolean = priority >= that
	def >=(that: LogLevel): Boolean = this.priority >= that.priority
	def matches(that: Int): Boolean = priority == that
	def matches(that: LogLevel): Boolean = this.priority == that.priority
}

case object Debug extends LogLevel(0)
case object Info extends LogLevel(1)
case object Warning extends LogLevel(2)
case object Error extends LogLevel(3)
case object Fatal extends LogLevel(4)
case object Off extends LogLevel(5)
