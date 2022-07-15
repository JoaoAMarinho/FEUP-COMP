.class public BasicMethods
.super Other

.method public func3()LBasicMethods;
	.limit stack 1
	.limit locals 3
	new BasicMethods
	astore_1
	aload_1
	astore_2
	aload_2
	invokespecial BasicMethods/<init>()V
	aload_2
	areturn
.end method


.method public <init>()V
    aload_0
    invokenonvirtual Other/<init>()V
    return
.end method

