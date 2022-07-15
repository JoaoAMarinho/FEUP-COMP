.class public ComplexArithmetic
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 35
	bipush 10
	istore_1
	iconst_2
	istore_2
	bipush 120
	istore_3
	iload_1
	istore 4
	iload_2
	istore 5
	iload 4
	iload 5
	idiv
	istore 6
	iload 6
	istore 7
	bipush 10
	istore 8
	iload 7
	iload 8
	imul
	istore 9
	iload 9
	istore 10
	iload_3
	iload 10
	isub
	istore 11
	iload 11
	istore 12
	bipush 12
	istore 13
	iconst_4
	istore 14
	iload 13
	iload 14
	imul
	istore 15
	iload 15
	istore 16
	iload 12
	iload 16
	iadd
	istore 17
	iload 17
	istore 18
	iconst_1
	istore 19
	iload 18
	iload 19
	iadd
	istore 20
	iload 20
	istore 21
	iconst_1
	istore 22
	iload 21
	iload 22
	isub
	istore 23
	iload 23
	istore 24
	iload_1
	istore 25
	iload_1
	istore 26
	iload 25
	iload 26
	imul
	istore 27
	iload 27
	istore 28
	iload_2
	istore 29
	iload 28
	iload 29
	idiv
	istore 30
	iload 30
	istore 31
	iload 24
	iload 31
	iadd
	istore 32
	iload 32
	istore 33
	iload 33
	istore 34
	iload 34
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

