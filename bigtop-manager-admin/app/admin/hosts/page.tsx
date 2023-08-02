"use client"

import {ColumnsType} from "antd/es/table";
import {Card, Divider, Progress, Statistic, Table} from "antd";

const App: React.FC = () => {
  interface DataType {
    key: string;
    name: string;
    ip: string;
    cores: string;
    memory: string;
    disk: number;
    load: string;
  }

  const dataSource: DataType[] = [
    {
      key: '255',
      name: `bigtop-manager-server`,
      ip: `192.168.0.255`,
      cores: '4',
      memory: '16GB',
      disk: Math.floor(Math.random() * 100),
      load: `${Math.floor(Math.random() * 100)}%`,
    }
  ]

  for (let i = 1; i < 235; i++) {
    dataSource.push({
      key: i.toString(),
      name: `bigtop-manager-agent-${i}`,
      ip: `192.168.0.${i}`,
      cores: '4',
      memory: '16GB',
      disk: Math.floor(Math.random() * 100),
      load: `${Math.floor(Math.random() * 100)}%`,
    });
  }

  const columns: ColumnsType<DataType> = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      align: 'center',
    },
    {
      title: 'IP Address',
      dataIndex: 'ip',
      key: 'ip',
      align: 'center',
    },
    {
      title: 'Cores',
      dataIndex: 'cores',
      key: 'cores',
      align: 'center',
    },
    {
      title: 'Memory',
      dataIndex: 'memory',
      key: 'memory',
      align: 'center',
    },
    {
      title: 'Disk Usage',
      dataIndex: 'disk',
      key: 'disk',
      align: 'center',
      render: (text: number) => <Progress strokeColor={'#52c41a'} percent={text} size="small"/>,
    },
    {
      title: 'Load Avg',
      dataIndex: 'load',
      key: 'load',
      align: 'center',
    },
  ];

  return (
    <>
      <Table dataSource={dataSource} columns={columns} />
    </>
  )
}

export default App;