import React from "react";
import {Input, Result} from "antd";
import {SmileOutlined} from "@ant-design/icons";

const Step1 = () => {
    return (
        <div className={"flex flex-col justify-center items-center content-center h-full"}>
            <Result
                icon={<SmileOutlined />}
                title="Let's get started!"
                subTitle={"Please follow the steps to create your cluster."}
                extra={<Input placeholder={"Input your cluster name"}/>}
            />
        </div>
    )
}

export default Step1;