.class public ComplexSubDiv
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 20
	bipush 10
	istore_1
	iconst_5
	istore_2
	bipush 10
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
	iload_3
	iload 7
	isub
	istore 8
	iload 8
	istore 9
	iload_1
	istore 10
	iload_2
	istore 11
	iload 10
	iload 11
	idiv
	istore 12
	iload 12
	istore 13
	iload 9
	iload 13
	isub
	istore 14
	iload 14
	istore 15
	iconst_1
	istore 16
	iload 15
	iload 16
	isub
	istore 17
	iload 17
	istore 18
	iload 18
	istore 19
	iload 19
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

