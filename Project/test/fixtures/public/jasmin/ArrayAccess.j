.class public ArrayAccess
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 28
	iconst_5
	istore_1
	iload_1
	newarray int
	astore_2
	iconst_0
	istore_3
	aload_2
	iload_3
	iconst_1
	iastore
	iconst_1
	istore 4
	aload_2
	iload 4
	iconst_2
	iastore
	iconst_2
	istore 5
	aload_2
	iload 5
	iconst_3
	iastore
	iconst_3
	istore 6
	aload_2
	iload 6
	iconst_4
	iastore
	iconst_4
	istore 7
	aload_2
	iload 7
	iconst_5
	iastore
	iconst_0
	istore 8
	aload_2
	astore 9
	aload 9
	iload 8
	iaload
	istore 10
	iload 10
	istore 11
	iload 11
	invokestatic ioPlus/printResult(I)V
	iconst_1
	istore 12
	aload_2
	astore 13
	aload 13
	iload 12
	iaload
	istore 14
	iload 14
	istore 15
	iload 15
	invokestatic ioPlus/printResult(I)V
	iconst_2
	istore 16
	aload_2
	astore 17
	aload 17
	iload 16
	iaload
	istore 18
	iload 18
	istore 19
	iload 19
	invokestatic ioPlus/printResult(I)V
	iconst_3
	istore 20
	aload_2
	astore 21
	aload 21
	iload 20
	iaload
	istore 22
	iload 22
	istore 23
	iload 23
	invokestatic ioPlus/printResult(I)V
	iconst_4
	istore 24
	aload_2
	astore 25
	aload 25
	iload 24
	iaload
	istore 26
	iload 26
	istore 27
	iload 27
	invokestatic ioPlus/printResult(I)V
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

