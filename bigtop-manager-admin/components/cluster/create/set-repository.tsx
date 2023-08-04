import React, { useContext, useEffect, useRef, useState } from 'react';
import type { InputRef } from 'antd';
import { Button, Form, Input, Popconfirm, Table } from 'antd';
import type { FormInstance } from 'antd/es/form';

const EditableContext = React.createContext<FormInstance | null>(null);

interface Item {
    key: string;
    os: string;
    name: string;
    baseUrl: string;
}

interface EditableRowProps {
    index: number;
}

const EditableRow: React.FC<EditableRowProps> = ({ index, ...props }) => {
    const [form] = Form.useForm();
    return (
      <Form form={form} component={false}>
          <EditableContext.Provider value={form}>
              <tr {...props} />
          </EditableContext.Provider>
      </Form>
    );
};

interface EditableCellProps {
    title: React.ReactNode;
    editable: boolean;
    children: React.ReactNode;
    dataIndex: keyof Item;
    record: Item;
    handleSave: (record: Item) => void;
}

const EditableCell: React.FC<EditableCellProps> = ({
                                                       title,
                                                       editable,
                                                       children,
                                                       dataIndex,
                                                       record,
                                                       handleSave,
                                                       ...restProps
                                                   }) => {
    const [editing, setEditing] = useState(false);
    const inputRef = useRef<InputRef>(null);
    const form = useContext(EditableContext)!;

    useEffect(() => {
        if (editing) {
            inputRef.current!.focus();
        }
    }, [editing]);

    const toggleEdit = () => {
        setEditing(!editing);
        form.setFieldsValue({ [dataIndex]: record[dataIndex] });
    };

    const save = async () => {
        try {
            const values = await form.validateFields();

            toggleEdit();
            handleSave({ ...record, ...values });
        } catch (errInfo) {
            console.log('Save failed:', errInfo);
        }
    };

    let childNode = children;

    if (editable) {
        childNode = editing ? (
          <Form.Item
            style={{ margin: 0 }}
            name={dataIndex}
            rules={[
                {
                    required: true,
                    message: `${title} is required.`,
                },
            ]}
          >
              <Input ref={inputRef} onPressEnter={save} onBlur={save} />
          </Form.Item>
        ) : (
          <div className={"cursor-pointer p-1 border-[1px] border-transparent group-hover:border-[#d9d9d9] border-solid rounded-md"} onClick={toggleEdit}>
              {children}
          </div>
        );
    }

    return <td {...restProps}>{childNode}</td>;
};

type EditableTableProps = Parameters<typeof Table>[0];

interface DataType {
    key: React.Key;
    os: string;
    name: string;
    baseUrl: string;
}

type ColumnTypes = Exclude<EditableTableProps['columns'], undefined>;

const SetRepository: React.FC = () => {
    const [dataSource, setDataSource] = useState<DataType[]>([
        {
            key: 'centos7',
            os: 'CentOS 7',
            name: 'BIGTOP-3.2.0',
            baseUrl: 'https://centos7.repo.com',
        },
        {
            key: 'rockylinux8',
            os: 'RockyLinux 7',
            name: 'BIGTOP-3.2.0',
            baseUrl: 'https://rockylinux8.repo.com',
        },
        {
            key: 'ubuntu20',
            os: 'Ubuntu 20.04',
            name: 'BIGTOP-3.2.0',
            baseUrl: 'https://ubuntu20.repo.com',
        },
        {
            key: 'ubuntu22',
            os: 'Ubuntu 22.04',
            name: 'BIGTOP-3.2.0',
            baseUrl: 'https://ubuntu22.repo.com',
        },
    ]);

    const defaultColumns: (ColumnTypes[number] & { editable?: boolean; dataIndex: string })[] = [
        {
            title: 'OS',
            dataIndex: 'os',
            align: 'center',
        },
        {
            title: 'Name',
            dataIndex: 'name',
            align: 'center',
        },
        {
            title: 'Base URL',
            dataIndex: 'baseUrl',
            align: 'center',
            editable: true,
        },
    ];

    const handleSave = (row: DataType) => {
        const newData = [...dataSource];
        const index = newData.findIndex((item) => row.key === item.key);
        const item = newData[index];
        newData.splice(index, 1, {
            ...item,
            ...row,
        });
        setDataSource(newData);
        console.log(newData)
    };

    const components = {
        body: {
            row: EditableRow,
            cell: EditableCell,
        },
    };

    const columns = defaultColumns.map((col) => {
        if (!col.editable) {
            return col;
        }
        return {
            ...col,
            onCell: (record: DataType) => ({
                record,
                editable: col.editable,
                dataIndex: col.dataIndex,
                title: col.title,
                className: "group",
                handleSave,
            }),
        };
    });

    return (
      <div className={"flex flex-col justify-start items-center content-center h-full"}>
          <div className={"text-2xl mb-4"}>Set Repository</div>
          <Table pagination={false} scroll={{y: 400}} components={components} dataSource={dataSource} columns={columns as ColumnTypes}/>
      </div>
    );
};

export default SetRepository;