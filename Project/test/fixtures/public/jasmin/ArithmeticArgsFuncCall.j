.class public ArithmeticArgsFuncCall
.super java/lang/Object

.method public func(III)I
	.limit stack 1
	.limit locals 7
	iload_1
	istore 4
	iload 4
	invokestatic ioPlus/printResult(I)V
	iload_2
	istore 5
	iload 5
	invokestatic ioPlus/printResult(I)V
	iload_3
	istore 6
	iload 6
	invokestatic ioPlus/printResult(I)V
	iconst_1
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 4
	.limit locals 26
	new ArithmeticArgsFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial ArithmeticArgsFuncCall/<init>()V
	bipush 10
	istore_3
	iconst_5
	istore 4
	bipush 100
	istore 5
	bipush 10
	istore 6
	iload 5
	iload 6
	idiv
	istore 7
	iload 7
	istore 8
	bipush 10
	istore 9
	iload 8
	iload 9
	iadd
	istore 10
	iload 10
	istore 11
	bipush 10
	istore 12
	iconst_2
	istore 13
	iload 12
	iload 13
	imul
	istore 14
	iload 14
	istore 15
	iload 11
	iload 15
	isub
	istore 16
	iload 16
	istore 17
	iload_3
	istore 18
	iload 4
	istore 19
	iload 18
	iload 19
	iadd
	istore 20
	iload 20
	istore 21
	iload_3
	istore 22
	iload 4
	istore 23
	iload 22
	iload 23
	isub
	istore 24
	iload 24
	istore 25
	aload_2
	iload 17
	iload 21
	iload 25
	invokevirtual ArithmeticArgsFuncCall/func(III)I
	istore_3
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

