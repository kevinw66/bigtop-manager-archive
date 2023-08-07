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
import {CustomIconComponentProps} from "@ant-design/icons/es/components/Icon";
import CircleFilled from "@/components/icons/circle-filled";

const AdminSider = () => {

  let [open, setOpen] = useState(false);

  const segments = useSelectedLayoutSegments();

  const HeartSvg = () => (
    <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
      <path d="M923 283.6c-13.4-31.1-32.6-58.9-56.9-82.8-24.3-23.8-52.5-42.4-84-55.5-32.5-13.5-66.9-20.3-102.4-20.3-49.3 0-97.4 13.5-139.2 39-10 6.1-19.5 12.8-28.5 20.1-9-7.3-18.5-14-28.5-20.1-41.8-25.5-89.9-39-139.2-39-35.5 0-69.9 6.8-102.4 20.3-31.4 13-59.7 31.7-84 55.5-24.4 23.9-43.5 51.7-56.9 82.8-13.9 32.3-21 66.6-21 101.9 0 33.3 6.8 68 20.3 103.3 11.3 29.5 27.5 60.1 48.2 91 32.8 48.9 77.9 99.9 133.9 151.6 92.8 85.7 184.7 144.9 188.6 147.3l23.7 15.2c10.5 6.7 24 6.7 34.5 0l23.7-15.2c3.9-2.5 95.7-61.6 188.6-147.3 56-51.7 101.1-102.7 133.9-151.6 20.7-30.9 37-61.5 48.2-91 13.5-35.3 20.3-70 20.3-103.3 0.1-35.3-7-69.6-20.9-101.9z" />
    </svg>
  );

  const HeartIcon = (props: Partial<CustomIconComponentProps>) => (
    <Icon component={HeartSvg} {...props} />
  );

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
      label: 'Add Service',
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
    </Sider>
  )
}

export default AdminSider;