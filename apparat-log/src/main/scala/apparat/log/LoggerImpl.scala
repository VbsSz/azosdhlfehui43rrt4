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

import java.io.{Writer => JWriter}

/**
 * @author Joa Ebert
 */
class LoggerImpl(level: LogLevel, outputs: List[LogOutput]) extends Logger {
	override def debug(format: String, arguments: Any*) = logIf(debugEnabled, Debug, format, arguments: _*)
	override def info(format: String, arguments: Any*) = logIf(infoEnabled, Info, format, arguments: _*)
	override def warning(format: String, arguments: Any*) = logIf(warningEnabled, Warning, format, arguments: _*)
	override def error(format: String, arguments: Any*) = logIf(errorEnabled, Error, format, arguments: _*)
	override def fatal(format: String, arguments: Any*) = logIf(fatalEnabled, Fatal, format, arguments: _*)

	override def log(level: LogLevel, message: String): Unit = level match {
		case Debug => if(debugEnabled) outputs foreach { _.log(level, message) }
		case Info => if(infoEnabled) outputs foreach { _.log(level, message) }
		case Warning => if(warningEnabled) outputs foreach { _.log(level, message) }
		case Error => if(errorEnabled) outputs foreach { _.log(level, message) }
		case Fatal => if(fatalEnabled) outputs foreach { _.log(level, message) }
		case Off =>
	}

	override val debugEnabled = Debug >= level
	override val infoEnabled = Info >= level
	override val warningEnabled = Warning >= level
	override val errorEnabled = Error >= level
	override val fatalEnabled = Fatal >= level

	override def asWriterFor(level: LogLevel): JWriter = new JWriter() {
		override def write(chars: Array[Char], off: Int, len: Int) = {
			if(len > 0 && (len > 1 || chars(off) != '\n' || chars(off) != '\r')) {
				val sb = new StringBuilder(len)

				var i = 0
				var n = len

				while(i < n && (chars(off + i) == '\r' || chars(off + i) == '\n')) {
					i += 1
				}

				while(n > 0 && (chars(off + n - 1) == '\r' || chars(off + n - 1) == '\n')) {
					n -= 1
				}

				while(i < n) {
					sb append chars(off + i)
					i += 1
				}

				val result = sb.toString

				if(result.nonEmpty &&
						result != "\r" && result != "\n" && result != "\n\r") {
					if(result.indexOf('\n') != -1) {
						result.split('\n') foreach { result => log(level, result) }
					} else {
						log(level, result)
					}
				}
			}
		}

		override def flush() = {}
		override def close() = {}
	}

	private def logIf(condition: Boolean, level: LogLevel, format: String, arguments: Any*) = if(condition) {
		val message = format.format(arguments: _*)
		outputs foreach { _.log(level, message) }
	}
}
