.class public SwitchStat
.super java/lang/Object

.method public func(I)I
	.limit stack 3
	.limit locals 27
	iload_1
	istore_2
	iconst_1
	istore_3
	iload_2
	iload_3
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 4
	iload 4
	ifne Then1
	iload_1
	istore 5
	iconst_2
	istore 6
	iload 5
	iload 6
	isub
	iflt ComparisonThen1
	iconst_0
	goto ComparisonEndIf1
	ComparisonThen1:
	iconst_1
	ComparisonEndIf1:
	istore 7
	iload 7
	ifne Then2
	iload_1
	istore 8
	iconst_3
	istore 9
	iload 8
	iload 9
	isub
	iflt ComparisonThen2
	iconst_0
	goto ComparisonEndIf2
	ComparisonThen2:
	iconst_1
	ComparisonEndIf2:
	istore 10
	iload 10
	ifne Then3
	iload_1
	istore 11
	iconst_4
	istore 12
	iload 11
	iload 12
	isub
	iflt ComparisonThen3
	iconst_0
	goto ComparisonEndIf3
	ComparisonThen3:
	iconst_1
	ComparisonEndIf3:
	istore 13
	iload 13
	ifne Then4
	iload_1
	istore 14
	iconst_5
	istore 15
	iload 14
	iload 15
	isub
	iflt ComparisonThen4
	iconst_0
	goto ComparisonEndIf4
	ComparisonThen4:
	iconst_1
	ComparisonEndIf4:
	istore 16
	iload 16
	ifne Then5
	iload_1
	istore 17
	bipush 6
	istore 18
	iload 17
	iload 18
	isub
	iflt ComparisonThen5
	iconst_0
	goto ComparisonEndIf5
	ComparisonThen5:
	iconst_1
	ComparisonEndIf5:
	istore 19
	iload 19
	ifne Then6
	bipush 7
	istore 20
	iload 20
	invokestatic ioPlus/printResult(I)V
	goto Endif6
	Then6:
	bipush 6
	istore 21
	iload 21
	invokestatic ioPlus/printResult(I)V
	Endif6:
	goto Endif5
	Then5:
	iconst_5
	istore 22
	iload 22
	invokestatic ioPlus/printResult(I)V
	Endif5:
	goto Endif4
	Then4:
	iconst_4
	istore 23
	iload 23
	invokestatic ioPlus/printResult(I)V
	Endif4:
	goto Endif3
	Then3:
	iconst_3
	istore 24
	iload 24
	invokestatic ioPlus/printResult(I)V
	Endif3:
	goto Endif2
	Then2:
	iconst_2
	istore 25
	iload 25
	invokestatic ioPlus/printResult(I)V
	Endif2:
	goto Endif1
	Then1:
	iconst_1
	istore 26
	iload 26
	invokestatic ioPlus/printResult(I)V
	Endif1:
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 11
	new SwitchStat
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial SwitchStat/<init>()V
	iconst_0
	istore_3
	aload_2
	iload_3
	invokevirtual SwitchStat/func(I)I
	istore 4
	iconst_1
	istore 5
	aload_2
	iload 5
	invokevirtual SwitchStat/func(I)I
	istore 4
	iconst_2
	istore 6
	aload_2
	iload 6
	invokevirtual SwitchStat/func(I)I
	istore 4
	iconst_3
	istore 7
	aload_2
	iload 7
	invokevirtual SwitchStat/func(I)I
	istore 4
	iconst_4
	istore 8
	aload_2
	iload 8
	invokevirtual SwitchStat/func(I)I
	istore 4
	iconst_5
	istore 9
	aload_2
	iload 9
	invokevirtual SwitchStat/func(I)I
	istore 4
	bipush 6
	istore 10
	aload_2
	iload 10
	invokevirtual SwitchStat/func(I)I
	istore 4
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

