/*
 * Sonar C++ Plugin (Community)
 * Copyright (C) 2010-2016 SonarOpenCommunity
 * http://github.com/SonarOpenCommunity/sonar-cxx
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.cxx.checks;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.io.Files;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.cxx.visitors.CxxCharsetAwareVisitor;
import org.sonar.squidbridge.checks.SquidCheck;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.api.Grammar;
import org.sonar.squidbridge.annotations.ActivatedByDefault;
import org.sonar.squidbridge.annotations.SqaleConstantRemediation;
import org.sonar.cxx.tag.Tag;

@Rule(
  key = "UseCorrectInclude",
  name = "#include directive shall not use relative path",
  tags = {Tag.PREPROCESSOR},
  priority = Priority.BLOCKER)
@ActivatedByDefault
@SqaleConstantRemediation("5min")
public class UseCorrectIncludeCheck extends SquidCheck<Grammar> implements CxxCharsetAwareVisitor {

  private static final String DEFAULT_REGULAR_EXPRESSION = "#include\\s+(?>\"|\\<)[\\\\/\\.]+";
  private static final String DEFAULT_MESSAGE = "Use correct #include directives";

  public String regularExpression = DEFAULT_REGULAR_EXPRESSION;
  public String message = DEFAULT_MESSAGE;
  private Pattern pattern;
  private Charset charset;

  @Override
  public void init() {
    if (null != regularExpression && !regularExpression.isEmpty()) {
      try {
        pattern = Pattern.compile(regularExpression, Pattern.DOTALL);
      } catch (RuntimeException e) {
        throw new IllegalStateException("Unable to compile regular expression: " + regularExpression, e);
      }
    }
  }

  @Override
  public void visitFile(AstNode astNode) {
    List<String> lines;
    try {
      lines = Files.readLines(getContext().getFile(), charset);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (pattern.matcher(line).find()) {
        getContext().createLineViolation(this, "Do not use relative path for #include directive.", i + 1);
      }
    }
  }

  @Override
  public void setCharset(Charset charset) {
    this.charset = charset;
  }

}
