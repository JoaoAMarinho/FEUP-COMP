.class public ComplexProd
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 14
	iconst_1
	istore_1
	bipush 7
	istore_2
	iload_1
	istore_3
	iload_2
	istore 4
	iload_3
	iload 4
	imul
	istore 5
	iload 5
	istore 6
	iload_2
	istore 7
	iload 6
	iload 7
	imul
	istore 8
	iload 8
	istore 9
	iconst_4
	istore 10
	iload 9
	iload 10
	imul
	istore 11
	iload 11
	istore 12
	iload 12
	istore 13
	iload 13
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

