.class public NoArgsFuncCall
.super java/lang/Object

.method public bar()I
	.limit stack 1
	.limit locals 4
	new NoArgsFuncCall
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial NoArgsFuncCall/<init>()V
	aload_2
	invokevirtual NoArgsFuncCall/bar()I
	istore_3
	iconst_1
	ireturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

