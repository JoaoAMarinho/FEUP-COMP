.class public Arithmetic_and
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 2
	.limit locals 7
	iconst_1
	istore_1
	iconst_0
	istore_2
	iload_1
	iload_2
	iadd
	iconst_2
	isub
	iflt ComparisonThen0
	iconst_1
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_0
	ComparisonEndIf0:
	istore_3
	iload_3
	istore 4
	iload 4
	ifne Then1
	iconst_0
	istore 5
	iload 5
	invokestatic io/print(I)V
	goto Endif1
	Then1:
	iconst_1
	istore 6
	iload 6
	invokestatic io/print(I)V
	Endif1:
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

