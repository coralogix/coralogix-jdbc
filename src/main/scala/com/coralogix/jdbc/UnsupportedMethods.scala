package com.coralogix.jdbc

import java.sql.SQLFeatureNotSupportedException

trait UnsupportedMethods { self =>

  private val className = self.getClass.getSimpleName
  private val packageName = self.getClass.getPackage.getName
  private val unsupportedClassName = s"$packageName.Unsupported"

  def unsupported = {
    val st = Thread.currentThread().getStackTrace
    val methodName = st.findLast(_.getClassName.startsWith(packageName)).get.getMethodName
    // line number in Unsupported...Methods class
    val lineNumber = st.findLast(_.getClassName.startsWith(unsupportedClassName)).get.getLineNumber
    throw new SQLFeatureNotSupportedException(s"$className.$methodName:$lineNumber")
  }

}
