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
package org.sonar.cxx.parser;

import static org.sonar.sslr.tests.Assertions.assertThat;

import org.junit.Test;

/**
 * @author jmecosta
 */
public class LamdaExpressionsTest extends ParserBaseTest {

  @Test
  public void lambdaExpression() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaExpression));

    mockRule(CxxGrammarImpl.lambdaIntroducer);
    mockRule(CxxGrammarImpl.lambdaDeclarator);
    mockRule(CxxGrammarImpl.compoundStatement);

    assertThat(p).matches("lambdaIntroducer compoundStatement");
    assertThat(p).matches("lambdaIntroducer lambdaDeclarator compoundStatement");
  }

  @Test
  public void lambdaExpression_reallife() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaExpression));

    assertThat(p).matches("[] ( ) { }");
    assertThat(p).matches("[] ( ) mutable { }");
    assertThat(p).matches("[] ( ) noexcept { }");
    assertThat(p).matches("[] ( ) throw(X,Y) { }");
    assertThat(p).matches("[] ( ) mutable noexcept { }");
    assertThat(p).matches("[] ( ) mutable throw(X,Y) { }");
    assertThat(p).matches("[] (int n) { }");
    assertThat(p).matches("[&] ( ) { }");
    assertThat(p).matches("[&foo] (int n) { }");
    assertThat(p).matches("[=] (int n) { }");
    assertThat(p).matches("[=,&foo] (int n) { }");
    assertThat(p).matches("[&foo1,&foo2,&foo3] (int n, int y, int z) { }");
    assertThat(p).matches("[] () throw () { }");
    assertThat(p).matches("[] () -> int { return 1; }");
    assertThat(p).matches("[] () -> long long { return 1; }");
    assertThat(p).matches("[] (const string& addr) { return addr.find( \".org\" ) != string::npos; }");
    assertThat(p).matches("[this] () { cout << _x; }");
    assertThat(p).matches("[] (int x, int y) -> int { return x + y; }");
    assertThat(p).matches("[](auto x, auto y) { return x + y; }");
    assertThat(p).matches("[](const auto& m) { return m.size(); }");
    assertThat(p).matches("[value = 1] { return value; }");
    assertThat(p).matches("[value = std::move(ptr)] { return *value; }");
    assertThat(p).matches("[&r = x, y = x+1] { r += 2; return y+2; }");
  }

  @Test
  public void lambdaIntroducer() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaIntroducer));
    mockRule(CxxGrammarImpl.lambdaCapture);

    assertThat(p).matches("[]");
    assertThat(p).matches("[lambdaCapture]");
  }

  @Test
  public void lambdaIntroducer_reallife() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaIntroducer));

    assertThat(p).matches("[&]");
    assertThat(p).matches("[=]");
    assertThat(p).matches("[bar]");
    assertThat(p).matches("[this]");
    assertThat(p).matches("[&foo]");
    assertThat(p).matches("[=,&foo]");
  }

  @Test
  public void lambdaCapture() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaCapture));
    mockRule(CxxGrammarImpl.captureDefault);
    mockRule(CxxGrammarImpl.captureList);

    assertThat(p).matches("captureDefault");
    assertThat(p).matches("captureList");
    assertThat(p).matches("captureDefault , captureList");
  }

  @Test
  public void captureDefault() {
    p.setRootRule(g.rule(CxxGrammarImpl.captureDefault));

    assertThat(p).matches("&");
    assertThat(p).matches("=");
  }

  @Test
  public void capture() {
    p.setRootRule(g.rule(CxxGrammarImpl.capture));

    assertThat(p).matches("foo");
    assertThat(p).matches("&foo");
    assertThat(p).matches("this");
    assertThat(p).matches("value = 1");
    assertThat(p).matches("u=move(u)");
    assertThat(p).matches("value = std::move(ptr)");
    assertThat(p).matches("&r = x, y = x+1");
  }

  @Test
  public void captureList() {
    p.setRootRule(g.rule(CxxGrammarImpl.captureList));
    mockRule(CxxGrammarImpl.capture);

    assertThat(p).matches("capture"); // or 1, optional out
    assertThat(p).matches("capture ..."); // or 1, optional in
    assertThat(p).matches("capture , capture"); // or 1, optional out
    assertThat(p).matches("capture , capture ..."); // or 1, optional in
  }

  @Test
  public void lambdaDeclarator() {
    p.setRootRule(g.rule(CxxGrammarImpl.lambdaDeclarator));
    mockRule(CxxGrammarImpl.parameterDeclarationClause);
    mockRule(CxxGrammarImpl.exceptionSpecification);
    mockRule(CxxGrammarImpl.attributeSpecifierSeq);
    mockRule(CxxGrammarImpl.trailingReturnType);

    assertThat(p).matches("( parameterDeclarationClause ) "); // all opt out
    assertThat(p).matches("( parameterDeclarationClause ) mutable"); // mutable in
    assertThat(p).matches("( parameterDeclarationClause ) exceptionSpecification"); // exceptionSpecification in
    assertThat(p).matches("( parameterDeclarationClause ) attributeSpecifierSeq"); // attributeSpecifierSeq in
    assertThat(p).matches("( parameterDeclarationClause ) trailingReturnType"); // trailingReturnType in
    assertThat(p).matches("( parameterDeclarationClause ) mutable exceptionSpecification"); // complex 1
    assertThat(p).matches("( parameterDeclarationClause ) mutable exceptionSpecification attributeSpecifierSeq"); // complex 2
    assertThat(p).matches("( parameterDeclarationClause ) mutable exceptionSpecification attributeSpecifierSeq trailingReturnType"); // complex
    // 3
    assertThat(p).matches("( parameterDeclarationClause ) exceptionSpecification attributeSpecifierSeq"); // complex 4
    assertThat(p).matches("( parameterDeclarationClause ) exceptionSpecification attributeSpecifierSeq trailingReturnType"); // complex 5
    assertThat(p).matches("( parameterDeclarationClause ) attributeSpecifierSeq trailingReturnType"); // complex 6
  }
}
