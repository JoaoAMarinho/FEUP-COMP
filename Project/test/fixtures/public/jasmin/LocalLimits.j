.class public LocalLimits
.super java/lang/Object

.method public func(II)I
	.limit stack 3
	.limit locals 17
	iload_2
	istore_3
	bipush 10
	istore 4
	iconst_3
	istore 5
	iconst_3
	istore 6
	iconst_4
	istore 7
	aload_0
	iload 6
	iload 7
	invokevirtual LocalLimits/func(II)I
	istore 8
	iload 5
	iload 8
	iadd
	istore 9
	iload 9
	istore 10
	iload 4
	iload 10
	imul
	istore 11
	iload 11
	istore 12
	iload_3
	iload 12
	iadd
	istore 13
	iload 13
	istore_1
	iconst_3
	istore 14
	iconst_4
	istore 15
	aload_0
	iload 14
	iload 15
	invokevirtual LocalLimits/func(II)I
	istore 16
	iconst_1
	ireturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

