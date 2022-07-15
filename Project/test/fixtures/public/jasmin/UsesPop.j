.class public UsesPop
.super java/lang/Object

.method public func5()I
	.limit stack 2
	.limit locals 3
	iconst_2
	istore_1
	aload_0
	invokevirtual UsesPop/func1()I
	pop
	iconst_0
	istore_2
	iconst_1
	ireturn
.end method

.method public func1()I
	.limit stack 1
	.limit locals 1
	iconst_1
	ireturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

