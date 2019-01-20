ruleset {
    description 'CodeNarc Rules'

    ruleset('rulesets/basic.xml') {
        'EmptyMethod' enabled: false
    }
    ruleset('rulesets/braces.xml')
    ruleset('rulesets/concurrency.xml')
    ruleset('rulesets/convention.xml') {
        'NoDef' enabled: false
        'MethodReturnTypeRequired' enabled: false
    }
    ruleset('rulesets/design.xml') {
        'AbstractClassWithoutAbstractMethod' enabled: false
        'AbstractClassWithPublicConstructor' enabled: false
    }
    ruleset('rulesets/exceptions.xml')
    ruleset('rulesets/formatting.xml') {
        'ClassJavadoc' enabled: false
        'FileEndsWithoutNewline' enabled: false
        'SpaceAroundMapEntryColon' enabled: false
        'ConsecutiveBlankLines' enabled: false
        'LineLength' enabled: false
    }
    ruleset('rulesets/generic.xml')
    ruleset('rulesets/groovyism.xml')
    ruleset('rulesets/imports.xml') {
        'MisorderedStaticImports' enabled: false
        'NoWildcardImports' enabled: false
    }
    ruleset('rulesets/jdbc.xml')
    //ruleset('rulesets/junit.xml')
    ruleset('rulesets/logging.xml')
    ruleset('rulesets/naming.xml') {
        'FactoryMethodName' enabled: false
        'FieldName' enabled: false
        'MethodName' enabled: false
    }
    ruleset('rulesets/security.xml')
    ruleset('rulesets/size.xml') {
        // enable it for cobertura
        'CrapMetric' enabled: false
        'CyclomaticComplexity' enabled: false
    }
    ruleset('rulesets/unnecessary.xml') {

        // disabling due to code narc bug => http://sourceforge.net/tracker/?func=detail&atid=1126573&aid=3524882&group_id=250145
        'UnnecessaryPackageReference' enabled: false
        'UnnecessaryReturnKeyword' enabled: false
        'UnnecessaryPublicModifier' enabled: false
        'UnnecessaryGString' enabled: false
        'UnnecessaryBooleanExpression' enabled: false
    }
    ruleset('rulesets/unused.xml') {
        'UnusedMethodParameter'  enabled: false
    }
}