.class public AddMultConstants
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 31
	iconst_1
	istore_1
	iconst_2
	istore_2
	iconst_3
	istore_3
	iload_2
	iload_3
	imul
	istore 4
	iload 4
	istore 5
	iload_1
	iload 5
	iadd
	istore 6
	iload 6
	istore 7
	iload 7
	invokestatic io/println(I)V
	iconst_1
	istore 8
	iconst_2
	istore 9
	iconst_3
	istore 10
	iload 9
	iload 10
	imul
	istore 11
	iload 11
	istore 12
	iload 8
	iload 12
	iadd
	istore 13
	iload 13
	istore 14
	iconst_5
	istore 15
	iload 14
	iload 15
	iadd
	istore 16
	iload 16
	istore 17
	iload 17
	invokestatic io/println(I)V
	iconst_1
	istore 18
	iconst_2
	istore 19
	iconst_3
	istore 20
	iload 19
	iload 20
	imul
	istore 21
	iload 21
	istore 22
	iload 18
	iload 22
	iadd
	istore 23
	iload 23
	istore 24
	bipush 6
	istore 25
	iconst_3
	istore 26
	iload 25
	iload 26
	idiv
	istore 27
	iload 27
	istore 28
	iload 24
	iload 28
	iadd
	istore 29
	iload 29
	istore 30
	iload 30
	invokestatic io/println(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

