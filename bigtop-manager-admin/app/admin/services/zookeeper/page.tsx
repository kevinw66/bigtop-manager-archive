"use client"

import {Button, Dropdown, MenuProps, Tabs, TabsProps} from "antd";
import {CaretRightOutlined, DownOutlined, PoweroffOutlined} from "@ant-design/icons";
import React from "react";
import ServiceConfiguration from "@/components/service/service-configuration";
import ServiceSummary from "@/components/service/service-summary";

const App: React.FC = () => {
  const onChange = (key: string) => {
    console.log(key);
  };

  const items: TabsProps['items'] = [
    {
      key: '1',
      label: `Summary`,
      children: <ServiceSummary />,
    },
    {
      key: '2',
      label: `Configuration`,
      children: <ServiceConfiguration />,
    },
  ];

  const menuItems: MenuProps['items'] = [
    {
      label: 'Start',
      key: '1',
      icon: <CaretRightOutlined  style={{fontSize: '16px', color:'#3f7524'}}/>,
    },
    {
      label: 'Stop',
      key: '2',
      icon: <PoweroffOutlined style={{fontSize: '16px', color:'#850305'}}/>,
    },
  ];

  const operations = <Dropdown menu={{items: menuItems}} placement={"bottom"}><Button className={"flex justify-between items-center"} type={"primary"}>Action <DownOutlined /></Button></Dropdown>;

  return (
    <Tabs className={"h-full"} defaultActiveKey="1" items={items} tabBarExtraContent={operations} onChange={onChange} />
  )
}

export default App;