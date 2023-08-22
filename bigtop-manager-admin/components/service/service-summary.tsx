import { CheckCircleFilled, CheckCircleTwoTone, CloseCircleFilled } from "@ant-design/icons";
import { Space } from "antd";
import Link from "next/link";

const ServiceSummary = () => {

  return (
    <div className={"flex justify-between h-full"}>
      <div className={"w-[80%] h-full"}>
        <div className={"text-2xl mb-4"}>Summary</div>
        <div className={"flex justify-start"}>
          <div className={"w-[20%] text-lg text-gray-500"}>Components</div>
          <div className={"flex flex-col justify-start"} >
            <Space className={"flex justify-start items-center"} size={40}>
              <div className={"flex flex-col justify-center items-center"}>
                <div className={"flex justify-center items-center w-full"}>
                  <CheckCircleFilled style={{ fontSize: '12px', color: '#52c41a' }} />
                  <div className={"ml-1 text-lg"}>3/3 Started</div>
                </div>
                <Link className={"visited:text-[#1677ff] text-[#1677ff]"} href="https://www.baidu.com">ZooKeeper Server</Link>
              </div>
              <div className={"flex flex-col justify-center items-center"}>
                <div className={"flex justify-center items-center w-full"}>
                  <CheckCircleFilled style={{ fontSize: '12px', color: '#52c41a' }} />
                  <div className={"ml-1 text-lg"}>3/3 Installed</div>
                </div>
                <Link className={"visited:text-[#1677ff] text-[#1677ff]"} href="https://www.baidu.com">ZooKeeper Client</Link>
              </div>
            </Space>
            <Space className={"flex justify-start items-center mt-10"} size={40}>
              <div className={"flex flex-col justify-center items-center"}>
                <div className={"flex justify-center items-center w-full"}>
                  <CloseCircleFilled style={{ fontSize: '12px', color: '#ff4d4f' }} />
                  <div className={"ml-1 text-lg"}>1/3 Started</div>
                </div>
                <Link className={"visited:text-[#1677ff] text-[#1677ff]"} href="https://www.baidu.com">ZooKeeper Server</Link>
              </div>
              <div className={"flex flex-col justify-center items-center"}>
                <div className={"flex justify-center items-center w-full"}>
                  <CheckCircleFilled style={{ fontSize: '12px', color: '#52c41a' }} />
                  <div className={"ml-1 text-lg"}>3/3 Installed</div>
                </div>
                <Link className={"visited:text-[#1677ff] text-[#1677ff]"} href="https://www.baidu.com">ZooKeeper Client</Link>
              </div>
              <div className={"flex flex-col justify-center items-center"}>
                <div className={"flex justify-center items-center w-full"}>
                  <CheckCircleFilled style={{ fontSize: '12px', color: '#52c41a' }} />
                  <div className={"ml-1 text-lg"}>3/3 Installed</div>
                </div>
                <Link className={"visited:text-[#1677ff] text-[#1677ff]"} href="https://www.baidu.com">HDFS Client</Link>
              </div>
            </Space>
          </div>

        </div>
      </div>
      <div className={"w-[20%] border-l-[1px] h-full pl-2"}>
        <div className={"text-2xl mb-4"}>Quick Links</div>
        <Space direction="vertical" size={2}>
          <Link href={"https://www.baidu.com"} rel="noopener noreferrer" target="_blank">NameNode</Link>
          <Link href={"https://www.baidu.com"} rel="noopener noreferrer" target="_blank">DataNode</Link>
          <Link href={"https://www.baidu.com"} rel="noopener noreferrer" target="_blank">ResourceManager</Link>
          <Link href={"https://www.baidu.com"} rel="noopener noreferrer" target="_blank">JobHistory</Link>
        </Space>
      </div>
    </div>
  )
}

export default ServiceSummary;