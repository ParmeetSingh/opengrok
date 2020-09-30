hostname -i
mvn test -Dmaven.surefire.debug -Dtest=CxxAnalyzerFactoryTest#testScopeAnalyzer -DfailIfNoTests=false
