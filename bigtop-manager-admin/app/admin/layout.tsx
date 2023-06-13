"use client"

import React from 'react';
import {
  BellOutlined,
  SettingFilled,
  SettingOutlined,
  SettingTwoTone,
  UploadOutlined,
  UserOutlined,
  VideoCameraOutlined
} from '@ant-design/icons';
import {Avatar, Badge, Dropdown, Layout, Menu, MenuProps, message, Space, theme} from 'antd';
import AdminFooter from "@/components/admin-footer";
import AdminSider from "@/components/admin-sider";
import AdminHeader from "@/components/admin-header";
import AdminContent from "@/components/admin-content";

const { Header, Content, Footer, Sider } = Layout;

export default function AdminLayout({children}: {
  children: React.ReactNode
}) {
  const {token: { colorBgContainer }} = theme.useToken();

  return (
    <Layout>
      <AdminSider />

      <Layout>
        <AdminHeader />

        <AdminContent children={children} />

        <AdminFooter />
      </Layout>
    </Layout>
  )
}