<?php

Table: ${table}
First column: ${firstColumn}
Last column: ${lastColumn}
Primary key: ${primaryKey}
Unqiue key: <#list unqiueKey as key>${key}<#if key_has_next>, </#if></#list>
Fields: <#list fields as field>${field}<#if field_has_next>, </#if></#list>

Columns:
<#list columns as column>

	Field: ${column.field}
	Type: ${column.type}
	Length: ${column.length}
	Null: ${column.null}
	Key: ${column.key}
	Default: ${column.default}
	Extra: ${column.extra}

</#list>
