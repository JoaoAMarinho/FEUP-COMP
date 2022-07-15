.class public SimpleWhileStat
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 10
	iconst_3
	istore_1
	iconst_0
	istore_2
	Loop1:
	iload_2
	istore_3
	iload_1
	istore 4
	iload_3
	iload 4
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 5
	iload 5
	ifne Body1
	goto EndLoop1
	Body1:
	iload_2
	istore 6
	iload 6
	invokestatic ioPlus/printResult(I)V
	iload_2
	istore 7
	iconst_1
	istore 8
	iload 7
	iload 8
	iadd
	istore 9
	iload 9
	istore_2
	goto Loop1
	EndLoop1:
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

