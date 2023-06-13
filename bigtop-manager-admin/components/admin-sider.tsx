import React from "react";
import {Avatar, Menu} from "antd";
import {UploadOutlined, UserOutlined, VideoCameraOutlined} from "@ant-design/icons";
import Sider from "antd/es/layout/Sider";

const AdminSider = () => {
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
        defaultSelectedKeys={['4']}
        items={[UserOutlined, VideoCameraOutlined, UploadOutlined, UserOutlined].map(
          (icon, index) => ({
            key: String(index + 1),
            icon: React.createElement(icon),
            label: `nav ${index + 1}`,
          }),
        )}
      />
    </Sider>
  )
}

export default AdminSider;