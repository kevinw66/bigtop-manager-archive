import React, {useState} from "react";
import {Divider, Input, Select, Space, Table} from "antd";
import {ColumnsType} from "antd/es/table";

const ChooseStack = () => {
  interface StackDataType {
    key: string;
    name: string;
    version: string;
  }

  const stackInfoData: StackDataType[] = [
    {
      key: 'hdfs',
      name: 'HDFS',
      version: '3.3.4-1',
    },
    {
      key: 'yarn',
      name: 'Yarn',
      version: '3.3.4-1',
    },
    {
      key: 'mapreduce2',
      name: 'MapReduce2',
      version: '3.3.4-1',
    },
    {
      key: 'tez',
      name: 'Tez',
      version: '0.10.1-1',
    },
    {
      key: 'hive',
      name: 'Hive',
      version: '3.1.3-1',
    },
    {
      key: 'hbase',
      name: 'HBase',
      version: '2.4.13-1',
    },
    {
      key: 'zookeeper',
      name: 'ZooKeeper',
      version: '3.5.9-2',
    },
    {
      key: 'kafka',
      name: 'Kafka',
      version: '2.8.1-2',
    },
    {
      key: 'spark',
      name: 'Spark',
      version: '3.2.3-1',
    },
    {
      key: 'zeppelin',
      name: 'Zeppelin',
      version: '0.10.1-1',
    },
    {
      key: 'flink',
      name: 'Flink',
      version: '1.15.3-1',
    },
    {
      key: 'solr',
      name: 'Solr',
      version: '8.11.2-1',
    },
    {
      key: 'kerberos',
      name: 'Kerberos',
      version: '1.10.3-30',
    },
  ];

  const stackInfoColumns: ColumnsType<StackDataType> = [
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      align: 'center',
      ellipsis: true,
    },
    {
      title: 'Version',
      dataIndex: 'version',
      key: 'version',
      align: 'center',
      ellipsis: true,
    }
  ];

  const stackData = ['BIGTOP', 'HDP'];

  const versionData = {
    'BIGTOP': ['3.2.0', '3.3.0'],
    'HDP': ['2.1.0', '2.2.0'],
  };

  type StackName = keyof typeof versionData;

  const [versions, setVersions] = useState(versionData[stackData[0] as StackName]);
  const [version, setVersion] = useState(versionData[stackData[0] as StackName][0]);

  const handleStackChange = (value: StackName) => {
    setVersions(versionData[value]);
    setVersion(versionData[value][0]);
  };

  const handleVersionChange = (value: string) => {
    setVersion(value);
  };

  interface RepositoryDataType {
    key: string;
    os: string;
    name: string;
    baseUrl: React.ReactNode;
  }

  const repositoryInfoData: RepositoryDataType[] = [
    {
      key: 'centos',
      os: 'CentOS 7',
      name: 'BIGTOP-3.2.0',
      baseUrl: <Input value={'https://your.repo.com'}/>,
    }
  ];

  const repositoryInfoColumns: ColumnsType<RepositoryDataType> = [
    {
      title: 'OS',
      dataIndex: 'os',
      key: 'os',
      align: 'center',
      ellipsis: true,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      align: 'center',
      ellipsis: true,
    },
    {
      title: 'Base URL',
      dataIndex: 'baseUrl',
      key: 'baseUrl',
      align: 'center',
      ellipsis: true,
    }
  ];

  return (
    <div className={"flex flex-col justify-center items-center content-center h-full"}>
      <div className={"text-2xl mb-4"}>Choose stack</div>
      <Space wrap className={"flex mb-4 items-start"}>
        <Select
          defaultValue={stackData[0] as StackName}
          style={{width: 100}}
          onChange={handleStackChange}
          options={stackData.map((stack) => ({label: stack, value: stack}))}
        />

        <Select
          value={version}
          style={{width: 80}}
          onChange={handleVersionChange}
          options={versions.map((version) => ({label: version, value: version}))}
        />
      </Space>

      <Table pagination={false} scroll={{y: 300}} columns={stackInfoColumns} dataSource={stackInfoData}/>
    </div>
  )
}

export default ChooseStack;