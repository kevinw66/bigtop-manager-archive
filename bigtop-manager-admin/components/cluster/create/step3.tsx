import React, {ReactNode} from "react";
import {Input, Progress, Result, Table} from "antd";
import {ColumnsType} from "antd/es/table";

const Step1 = () => {
    interface DataType {
        key: string;
        host: string;
        progress: ReactNode;
    }

    const data: DataType[] = [
        {
            key: '1',
            host: 'bigtop-manager-server',
            progress: <Progress className={"w-4/5"} percent={50} status="active" strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }} />,
        },
        {
            key: '2',
            host: 'bigtop-manager-agent-01',
            progress: <Progress className={"w-4/5"} percent={90} status="active" strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }} />,
        },
        {
            key: '3',
            host: 'bigtop-manager-agent-02',
            progress: <Progress className={"w-4/5"} percent={70} status="active" strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }} />,
        },
        {
            key: '4',
            host: 'bigtop-manager-agent-03',
            progress: <Progress className={"w-4/5"} percent={100} strokeColor={{ '0%': '#108ee9', '100%': '#87d068' }} />,
        },
    ];

    const columns: ColumnsType<DataType> = [
        {
            title: 'Host',
            dataIndex: 'host',
            key: 'host',
            align: 'center',
            ellipsis: true,
        },
        {
            title: 'Progress',
            dataIndex: 'progress',
            key: 'progress',
            align: 'center',
            ellipsis: true,
        },

    ];

    return (
        <div className={"content-center h-full mt-4"}>
            <Table pagination={false} columns={columns} dataSource={data} />
        </div>
    )
}

export default Step1;