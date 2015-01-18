ruleset {
    ruleset('file:config/codenarc/StarterRuleSet-AllRulesByCategory.groovy') {
        ClassJavadoc(enabled: false)
        ClosureAsLastMethodParameter(enabled: false)
        CrapMetric(enabled: false)
        DuplicateNumberLiteral(enabled: false)
        DuplicateStringLiteral(enabled: false)
        FactoryMethodName(enabled: false)
        LineLength(length: 140)
        MethodName(enabled: false)
        NoDef(enabled: false)
        PrivateFieldCouldBeFinal(enabled: false)
        SpaceAroundMapEntryColon(enabled: false)
        UnnecessaryBooleanExpression(enabled: false)
        UnusedObject(enabled: false)
    }
}