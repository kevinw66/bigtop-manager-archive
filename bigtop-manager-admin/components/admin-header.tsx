import React, {useState} from "react";
import {Header} from "antd/es/layout/layout";
import {Avatar, Badge, Button, Dropdown, MenuProps, message, Modal, Space} from "antd";
import {BellOutlined, SettingOutlined, UserOutlined} from "@ant-design/icons";
import ClusterCreateModal from "@/components/cluster/create/cluster-create-modal";

const AdminHeader = () => {

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

  const onClick: MenuProps['onClick'] = ({ key }) => {
    message.info(`Click on item ${key}`);
  };

  const [openClusterCreateModal, setOpenClusterCreateModal] = useState(false);

  const items: MenuProps['items'] = [
    {
      label: 'Switch cluster',
      key: 'switch',
    },
    getItem(<a onClick={() => setOpenClusterCreateModal(true)}>Create cluster</a> , 'create'),
  ];

  const userItems: MenuProps['items'] = [
    {
      label: 'About',
      key: '1',
    },
    {
      type: 'divider',
    },
    {
      label: 'Log out',
      key: '3',
    }
  ];

  return (
    <Header className="bg-white flex justify-end items-center">
      <Space className="flex justify-center items-center" size={16}>
        <Dropdown menu={{ items }} placement="bottom">
          <a onClick={(e) => e.preventDefault()}>
            Cluster A
          </a>
        </Dropdown>

        <Badge className="cursor-pointer" size="small" color="blue" count={1}>
          <SettingOutlined style={{ fontSize: '20px' }}/>
        </Badge>

        <Badge className="cursor-pointer" size="small" color="red" count={1}>
          <BellOutlined style={{ fontSize: '20px' }}/>
        </Badge>

        <Dropdown menu={{ items: userItems, onClick }} placement="bottom">
          <a onClick={(e) => e.preventDefault()}>
            <Avatar icon={<UserOutlined />} />
          </a>
        </Dropdown>
      </Space>

      <ClusterCreateModal open={openClusterCreateModal} setOpen={setOpenClusterCreateModal}/>
    </Header>
  )
}

export default AdminHeader;