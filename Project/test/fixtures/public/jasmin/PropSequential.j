.class public PropSequential
.super java/lang/Object

.method public foo(I)I
	.limit stack 2
	.limit locals 6
	bipush 10
	istore_2
	iload_1
	istore_3
	iload_2
	iload_3
	imul
	istore 4
	iload 4
	istore 5
	iload 5
	ireturn
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 0
	.limit locals 1
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

