"use client"

import React from 'react';
import {Layout} from 'antd';
import AdminFooter from "@/components/admin-footer";
import AdminSider from "@/components/admin-sider";
import AdminHeader from "@/components/admin-header";
import AdminContent from "@/components/admin-content";

export default function AdminLayout({children}: {
  children: React.ReactNode
}) {
  return (
    <Layout>
      <AdminSider />

      <Layout>
        <AdminHeader />

        <AdminContent>{children}</AdminContent>

        <AdminFooter />
      </Layout>
    </Layout>
  )
}