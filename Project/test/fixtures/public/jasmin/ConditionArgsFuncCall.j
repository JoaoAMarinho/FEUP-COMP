.class public ConditionArgsFuncCall
.super java/lang/Object

.method public func(ZZZZ)I
	.limit stack 1
	.limit locals 6
	bipush 10
	istore 5
	iload 5
	invokestatic ioPlus/printResult(I)V
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 5
	.limit locals 23
	new ConditionArgsFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial ConditionArgsFuncCall/<init>()V
	bipush 10
	istore_3
	iconst_5
	istore 4
	iconst_1
	istore 5
	iconst_0
	istore 6
	iload_3
	istore 7
	iload 4
	istore 8
	iload 7
	iload 8
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 9
	iload 9
	istore 10
	iload 5
	istore 11
	iload 6
	istore 12
	iload 11
	iload 12
	iadd
	iconst_2
	isub
	iflt ComparisonThen1
	iconst_1
	goto ComparisonEndIf1
	ComparisonThen1:
	iconst_0
	ComparisonEndIf1:
	istore 13
	iload 13
	istore 14
	iload_3
	istore 15
	iload 4
	istore 16
	iload 15
	iload 16
	isub
	iflt ComparisonThen2
	iconst_0
	goto ComparisonEndIf2
	ComparisonThen2:
	iconst_1
	ComparisonEndIf2:
	istore 17
	iload 17
	istore 18
	iload 5
	istore 19
	iload 18
	iload 19
	iadd
	iconst_2
	isub
	iflt ComparisonThen3
	iconst_1
	goto ComparisonEndIf3
	ComparisonThen3:
	iconst_0
	ComparisonEndIf3:
	istore 20
	iload 20
	istore 21
	iload 5
	ifne Then4
	iconst_1
	goto EndIf4
	Then4:
	iconst_0
	EndIf4:
	istore 22
	aload_2
	iload 10
	iload 14
	iload 21
	iload 22
	invokevirtual ConditionArgsFuncCall/func(ZZZZ)I
	istore_3
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

