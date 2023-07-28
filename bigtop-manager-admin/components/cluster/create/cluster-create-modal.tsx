import {Button, Modal, StepProps, Steps} from "antd";
import React, {Dispatch, SetStateAction, useState} from "react";
import {ExclamationCircleFilled} from "@ant-design/icons";
import Step1 from "@/components/cluster/create/step1";
import Step2 from "@/components/cluster/create/step2";
import Step3 from "@/components/cluster/create/step3";
import Step4 from "@/components/cluster/create/step4";

const ClusterCreateModal = ({open, setOpen}: {
  open: boolean,
  setOpen: Dispatch<SetStateAction<boolean>>
}) => {

  const steps = [
    {
      status: 'process',
      title: 'Step 1',
      content: <Step1 />,
    },
    {
      status: 'wait',
      title: 'Step 2',
      content: <Step2 />,
    },
    {
      status: 'wait',
      title: 'Step 3',
      content: <Step3 />,
    },
    {
      status: 'wait',
      title: 'Step 4',
      content: <Step4 />,
    },
  ];

  const [current, setCurrent] = useState(0);
  const [items, setItems] = useState(steps);

  const next = () => {
    items[current].status = 'finish';
    setCurrent(current + 1);
    items[current + 1].status = 'process';
    setItems(items);
  };
  const prev = () => {
    items[current].status = 'wait';
    setCurrent(current - 1);
    items[current - 1].status = 'process';
    setItems(items);
  };

  const handleCancel = () => {
    Modal.confirm({
      title: 'Exit',
      icon: <ExclamationCircleFilled />,
      content: 'Are you sure you want to exit?',
      onOk() {
        setOpen(false);
        setCurrent(0);
        setItems(steps);
      },
      onCancel() {
        console.log('Cancel');
      },
    });
  };

  const handleSubmit = () => {
    setOpen(false);
    setCurrent(0);
    setItems(steps);
  };

  return (
    <Modal width="80%"
           open={open}
           centered={true}
           onCancel={handleCancel}
           maskClosable={false}
           keyboard={false}
           footer={[
             <div>
               {current > 0 && (
                 <Button type="primary" className={"w-1/12"} onClick={() => prev()}>
                   Previous
                 </Button>
               )}

               {current < items.length - 1 && (
                 <Button type="primary" className={"w-1/12"} onClick={() => next()}>
                   Next
                 </Button>
               )}

               {current === items.length - 1 && (
                 <Button type="primary" className={"w-1/12"} onClick={() => handleSubmit()}>
                   Done
                 </Button>
               )}
             </div>
           ]}
    >
      <div>
        <Steps
          type="navigation"
          // direction={"vertical"}
          // className={"w-1/5"}
          current={current}
          // className="site-navigation-steps"
          items={items as StepProps[]}
        />
        <div className="mt-4 h-[35rem] w-full text-center">{steps[current].content}</div>
      </div>
    </Modal>
  )
}

export default ClusterCreateModal;