.class public Arithmetic_add
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 6
	iconst_2
	istore_1
	iconst_3
	istore_2
	iload_1
	iload_2
	iadd
	istore_3
	iload_3
	istore 4
	iload 4
	istore 5
	iload 5
	invokestatic io/print(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

