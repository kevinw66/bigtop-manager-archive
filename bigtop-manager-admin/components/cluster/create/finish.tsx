import React from "react";
import {Input, Result} from "antd";
import {LikeOutlined, SmileOutlined} from "@ant-design/icons";

const Finish = () => {
    return (
        <div className={"flex flex-col justify-center items-center content-center h-full"}>
            <Result
                status={"success"}
                title="Congratulations!"
                subTitle={"Cluster created successfully, please enjoy it."}
            />
        </div>
    )
}

export default Finish;