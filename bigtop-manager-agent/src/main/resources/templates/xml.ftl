<?xml version="1.0" encoding="UTF-8"?>
<!-- Generated by Apache BigTop Manager. -->
<configuration xmlns:xi="http://www.w3.org/2001/XInclude">
<#list model as key,value>
    <property>
        <name>${key}</name>
        <value>${value?c}</value>
    </property>
</#list>
</configuration>