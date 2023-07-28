import React from "react";
import {Input, Result} from "antd";
import {LikeOutlined, SmileOutlined} from "@ant-design/icons";

const Step1 = () => {
    return (
        <div className={"flex flex-col justify-center items-center content-center h-full"}>
            <Result
                icon={<LikeOutlined />}
                title="Congratulations!"
                subTitle={"Cluster created successfully, please enjoy it."}
            />
        </div>
    )
}

export default Step1;