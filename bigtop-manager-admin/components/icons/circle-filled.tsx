import Icon from "@ant-design/icons";
import type {CustomIconComponentProps} from "@ant-design/icons/es/components/Icon";

const CircleFilledSvg = () => (
  <svg width="1em" height="1em" fill="currentColor" viewBox="0 0 1024 1024">
    <path d="M512 16C238 16 16 238 16 512s222 496 496 496 496-222 496-496S786 16 512 16z"/>
  </svg>
);

const CircleFilled = (props: Partial<CustomIconComponentProps>) => (
  <Icon component={CircleFilledSvg} {...props} />
);

export default CircleFilled;