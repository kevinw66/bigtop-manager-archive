import React, {useState} from "react";
import {Button, Divider, Select, Space, Table} from "antd";
import {ColumnsType} from "antd/es/table";
import TextArea from "antd/es/input/TextArea";

const Step2 = () => {
    interface StackDataType {
        key: string;
        name: string;
        version: string;
    }

    const stackInfoData: StackDataType[] = [
        {
            key: '1',
            name: 'Flink',
            version: '1.15.3',
        },
        {
            key: '2',
            name: 'Spark',
            version: '3.2.3',
        },
        {
            key: '3',
            name: 'Hive',
            version: '3.1.3',
        },
        {
            key: '4',
            name: 'Hadoop',
            version: '3.3.4',
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
        baseUrl: string;
    }

    const repositoryInfoData: RepositoryDataType[] = [
        {
            key: '1',
            os: 'CentOS 7',
            name: 'BIGTOP-3.2.0',
            baseUrl: 'https://your.repo.com',
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
        <div className={"content-center h-full overflow-y-scroll"}>
            <div className={"text-2xl mb-4"}>Stacks</div>
            <Space wrap className={"flex mb-4 items-start"}>
                <Select
                    defaultValue={stackData[0] as StackName}
                    style={{ width: 100 }}
                    onChange={handleStackChange}
                    options={stackData.map((stack) => ({ label: stack, value: stack }))}
                />

                <Select
                    value={version}
                    style={{ width: 80 }}
                    onChange={handleVersionChange}
                    options={versions.map((version) => ({ label: version, value: version }))}
                />
            </Space>

            <Table pagination={false} scroll={{ y: 200 }} columns={stackInfoColumns} dataSource={stackInfoData} />

            <Divider dashed={true}/>

            <div className={"text-2xl mb-4"}>Repositories</div>

            <Table pagination={false} scroll={{ y: 200 }} columns={repositoryInfoColumns} dataSource={repositoryInfoData} />

            <Divider dashed={true}/>

            <div className={"text-2xl mb-4"}>Hosts</div>

            <TextArea rows={6} placeholder={"Enter a list of hosts, one per line."}/>

        </div>
    )
}

export default Step2;