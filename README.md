# 后台服务


## 分层介绍


### Controller

负责业务调度的，所以在这一层应写一些业务的调度代码，而具体的业务处理应放在service中去写，而且service不单纯是对于dao的增删改查的调用，service是业务层，所以应该更切近于具体业务功能要求，所以在这一层，一个方法所体现的是一个可以对外提供的功能，比如购物商城中的生成订单方法，这里面就不简单是增加个订单记录那么简单，我们需要查询库存，核对商品等一系列实际业务逻辑的处理；


### Service

主要负责业务模块的逻辑。一般service层我们会先写一个interface，这个接口以service为后缀表示这是一个service接口，在这个类里定义好我们需要的方法，然后写实现类去实现这个接口里的方法，这样可以做到高度解耦合，非常nice的编程习惯。service层编写完成后，我们就可以在controller层中调用Service接口来进行业务处理。Service层的业务实现，具体要调用到已定义的DAO层的接口，封装Service层的业务逻辑有利于通用的业务逻辑的独立性和重复利用性，程序显得非常简洁。

### Entity

用于存放的实体类,与数据库中的属性值基本保持一致


### DAO(mapper)

即 data access object 数据访问对象。
为了简化业务逻辑的编写。将业务逻辑中用于处理特定技术的代码，单独写入到Dao中进行封装，从而尽量将业务逻辑的主要过程独立的进行表达

### DTO
用于表现层和应用层之间的数据交互 简单来说Model面向业务，我们是通过业务来定义Model的。而DTO是面向界面UI，是通过UI的需求来定义的。 通过DTO我们实现了表现层与Model之间的解耦，表现层不引用Model



## 单点登录

采用`前端不同域 + 后端不同 Redis`方式，通过Http请求获取会话。


用户在 子系统 点击 [登录] 按钮。
用户跳转到子系统登录接口 /sso/login，并携带 back参数 记录初始页面URL。
形如：http://{sso-client}/sso/login?back=xxx
子系统检测到此用户尚未登录，再次将其重定向至SSO认证中心，并携带redirect参数记录子系统的登录页URL。
形如：http://{sso-server}/sso/auth?redirect=xxx?back=xxx
用户进入了 SSO认证中心 的登录页面，开始登录。
用户 输入账号密码 并 登录成功，SSO认证中心再次将用户重定向至子系统的登录接口/sso/login，并携带ticket码参数。
形如：http://{sso-client}/sso/login?back=xxx&ticket=xxxxxxxxx
子系统根据 ticket码 从 SSO-Redis 中获取账号id，并在子系统登录此账号会话。
子系统将用户再次重定向至最初始的 back 页面。