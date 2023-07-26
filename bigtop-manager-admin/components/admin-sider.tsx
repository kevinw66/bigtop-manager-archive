import React from "react";
import {Avatar, Menu, MenuProps} from "antd";
import {
  AppstoreOutlined, BarsOutlined,
  ContainerOutlined,
  DesktopOutlined, MailOutlined,
  PieChartOutlined,
} from "@ant-design/icons";
import Sider from "antd/es/layout/Sider";
import Link from "next/link";

const AdminSider = () => {

  type MenuItem = Required<MenuProps>['items'][number];

  function getItem(
    label: React.ReactNode,
    key: React.Key,
    icon?: React.ReactNode,
    children?: MenuItem[],
    type?: 'group',
  ): MenuItem {
    return {
      key,
      icon,
      children,
      label,
      type,
    } as MenuItem;
  }

  // noinspection HtmlUnknownTarget
  const items: MenuItem[] = [
    getItem(<Link href="/admin/dashboard">Dashboard</Link> , 'dashboard', <PieChartOutlined />),
    getItem('Option 2', '2', <DesktopOutlined />),
    getItem('Option 3', '3', <ContainerOutlined />),

    getItem('Services', 'services', <AppstoreOutlined />, [
      getItem(<Link href="/admin/services/zookeeper">ZooKeeper</Link>, 'zookeeper'),
      getItem('Option 6', '6'),
      getItem('Option 7', '7'),
      getItem('Option 8', '8'),
    ]),

    getItem('Navigation Two', 'sub2', <AppstoreOutlined />, [
      getItem('Option 9', '9'),
      getItem('Option 10', '10'),

      getItem('Submenu', 'sub3', null, [getItem('Option 11', '11'), getItem('Option 12', '12')]),
    ]),
  ];

  return (
    <Sider
      width="250"
      breakpoint="lg"
      collapsedWidth="0"
      onBreakpoint={(broken) => {
        console.log(broken);
      }}
      onCollapse={(collapsed, type) => {
        console.log(collapsed, type);
      }}
    >
      <div className="flex h-8 m-4 rounded-md justify-center items-center">
        <Avatar size={32} src={<img src="/logo.png" alt="avatar"/>}/>
        <p className="flex justify-center items-center ml-2 text-base font-semibold text-gray-300">
          Bigtop Manager
        </p>
      </div>
      <Menu
        theme="dark"
        mode="inline"
        defaultSelectedKeys={['dashboard']}
        items={items}
      />
    </Sider>
  )
}

export default AdminSider;