.class public A
.super java/lang/Object

.field a I
.method public static main([Ljava/lang/String;)V
	.limit stack 1
	.limit locals 3
	bipush 10
	istore_1
	bipush 10
	istore_2
	iload_2
	invokestatic io/print(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

