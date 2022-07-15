.class public WhileOpt
.super java/lang/Object

.method public static main([Ljava/lang/String;)V
	.limit stack 1
	.limit locals 2
	Loop1:
	iconst_1
	istore_1
	iconst_1
	ifne Loop1
	return
.end method


.method public <init>()V
    aload_0
    invokenonvirtual java/lang/Object/<init>()V
    return
.end method

