import React, {useState} from "react";
import {Avatar, Dropdown, Menu, MenuProps} from "antd";
import Icon, {
  AppstoreOutlined,
  CaretRightOutlined,
  ContainerOutlined,
  DesktopOutlined,
  PieChartOutlined,
  PlusOutlined,
  PoweroffOutlined,
} from "@ant-design/icons";
import Sider from "antd/es/layout/Sider";
import Link from "next/link";
import {useSelectedLayoutSegments} from "next/navigation";
import CircleFilled from "@/components/icons/circle-filled";
import ServiceAddModal from "./service/add/service-add-modal";

const AdminSider = () => {

  let [open, setOpen] = useState(false);
  const [openServiceAddModal, setOpenServiceAddModal] = useState(false);

  const segments = useSelectedLayoutSegments();

  type MenuItem = Required<MenuProps>['items'][number];

  function getItem(
    label: React.ReactNode,
    key: React.Key,
    icon?: React.ReactNode,
    children?: MenuItem[],
    type?: 'group' | 'divider',
  ): MenuItem {
    return {
      key,
      icon,
      children,
      label,
      type,
    } as MenuItem;
  }

  const sitems: MenuProps['items'] = [
    {
      label: <a onClick={() => setOpenServiceAddModal(true)}>Add Service</a>,
      key: 'add',
      icon: <PlusOutlined style={{fontSize: '16px', color:'#3f7524'}}/>,
    },
    {
      type: 'divider',
    },
    {
      label: 'Start All',
      key: 'start',
      icon: <CaretRightOutlined  style={{fontSize: '16px', color:'#3f7524'}}/>,
    },
    {
      label: 'Stop All',
      key: 'stop',
      icon: <PoweroffOutlined style={{fontSize: '16px', color:'#850305'}}/>,
    },
  ];

  const items: MenuItem[] = [
    getItem(<Link href={{ pathname: "/admin/dashboard" }}>Dashboard</Link>, 'dashboard', <PieChartOutlined/>),
    getItem(<Link href={{ pathname: "/admin/hosts" }}>Hosts</Link>, 'hosts', <DesktopOutlined/>),
    getItem('Option 3', '3', <ContainerOutlined/>),

    getItem(
      <div className={"flex justify-between items-center"}>
        <div className={"w-4/5"}>Services</div>
        <Dropdown open={open} onOpenChange={() => {setOpen(!open)}} className={"pr-1"} menu={{items: sitems}} dropdownRender={(menu) => (
          <div onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
            setOpen(!open);
          }}>
            {React.cloneElement(menu as React.ReactElement)}
          </div>
        )} placement="bottom" trigger={['click']}>
          <a onClick={(e) => {
            e.preventDefault();
            e.stopPropagation();
          }}>···</a>
        </Dropdown>
      </div>, 'services', <AppstoreOutlined/>, [
        getItem(<div className={"flex"}>
          <CircleFilled style={{ fontSize: '8px', color:'#52c41a' }}/>
          <Link href={{ pathname: "/admin/services/zookeeper" }}>ZooKeeper</Link></div>, 'zookeeper'),
        getItem('Option 6', '6'),
        getItem('Option 7', '7'),
        getItem('Option 8', '8'),
      ]),

    getItem('Navigation Two', 'sub2', <AppstoreOutlined/>, [
      getItem('Option 9', '9'),
      getItem('Option 10', '10'),

      getItem('Submenu', 'sub3', null, [getItem('Option 11', '11'), getItem('Option 12', '12')]),
    ]),

    getItem('Navigation Three', 'sub4', <AppstoreOutlined/>, []),
  ];

  return (
    <Sider
      width="15%"
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
        openKeys={['services']}
        selectedKeys={[segments[segments.length - 1]]}
        items={items}
        onClick={(e) => console.log(e)}
      />

      <ServiceAddModal open={openServiceAddModal} setOpen={setOpenServiceAddModal}/>
    </Sider>
  )
}

export default AdminSider;