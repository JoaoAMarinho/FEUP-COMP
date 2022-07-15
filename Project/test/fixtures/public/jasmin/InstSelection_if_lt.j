.class public InstSelection_if_lt
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 3
	.limit locals 5
	iconst_0
	istore_1
	iload_1
	istore_2
	iconst_0
	istore_3
	iload_2
	iload_3
	isub
	iflt ComparisonThen0
	iconst_0
	goto ComparisonEndIf0
	ComparisonThen0:
	iconst_1
	ComparisonEndIf0:
	istore 4
	iload 4
	ifne Then1
	iconst_2
	istore_1
	goto Endif1
	Then1:
	iconst_1
	istore_1
	Endif1:
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

