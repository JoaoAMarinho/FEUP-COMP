.class public SimpleIfElseStat
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 13
	iconst_5
	istore_1
	bipush 10
	istore_2
	iload_1
	istore_3
	iload_2
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
	ifne Then1
	iload_2
	istore 6
	iload 6
	invokestatic ioPlus/printResult(I)V
	goto Endif1
	Then1:
	iload_1
	istore 7
	iload 7
	invokestatic ioPlus/printResult(I)V
	Endif1:
	bipush 10
	istore_1
	bipush 8
	istore_2
	iload_1
	istore 8
	iload_2
	istore 9
	iload 8
	iload 9
	isub
	iflt ComparisonThen1
	iconst_0
	goto ComparisonEndIf1
	ComparisonThen1:
	iconst_1
	ComparisonEndIf1:
	istore 10
	iload 10
	ifne Then2
	iload_2
	istore 11
	iload 11
	invokestatic ioPlus/printResult(I)V
	goto Endif2
	Then2:
	iload_1
	istore 12
	iload 12
	invokestatic ioPlus/printResult(I)V
	Endif2:
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

