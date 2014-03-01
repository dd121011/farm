/**
 * config
 */

var path = require('path');

exports.config = {
  debug: true,
  name: 'jiaoyouke',
  description: '郊游客管理端',
  version: '0.0.1',

  // site settings
  site_headers: [
    '<meta name="author" content="lifengyong@gmail.com" />',
  ],
  host: 'jiaoyouke.com',
  site_logo: '', // default is `name`
  site_navs: [
    // [ path, title, [target=''] ]
    [ '/about', '关于' ],
  ],
  site_static_host: '', // 静态文件存储域名
  site_enable_search_preview: false, // 开启google search preview
  site_google_search_domain:  'jiaoyouke.com',  // google search preview中要搜索的域名

  upload_dir: path.join(__dirname, 'public', 'user_data', 'images'),

  db: 'mongodb://127.0.0.1/node_club_dev',
  session_secret: 'oriental_leaves',
  auth_cookie_name: 'oriental_leaves',
  port: 3000,
  
  //mysql config
  dbIp: '172.20.6.19',
  dbPort: '3306',
  dbUser: 'root',
  dbPassword: '123',

  // 话题列表显示的话题数量
  list_topic_count: 20,

  // RSS
  rss: {
    title: 'jiaoyouke：郊游客',
    link: 'http://www.jiaoyouke.com',
    language: 'zh-cn',
    description: 'jiaoyouke_manager：郊游客管理端',

    //最多获取的RSS Item数量
    max_rss_items: 50
  },
 
  // site links
  site_links: [
    {
      'text': 'Node 官方网站',
      'url': 'http://nodejs.org/'
    }
  ],

  // sidebar ads
  side_ads: [
    {
      'url': 'http://www.jiaoyouke.com',
      'image': 'http://farmhome.b0.upaiyun.com/logo.png',
      'text': ''
    }
  ],

  // mail SMTP
  mail_port: 25,
  mail_user: 'club',
  mail_pass: 'club',
  mail_host: 'smtp.126.com',
  mail_sender: 'club@126.com',
  mail_use_authentication: true,
  
  //weibo app key
  weibo_key: 10000000,

  // admin 可删除话题，编辑标签，设某人为达人
  admins: { admin: true },

  // [ { name: 'plugin_name', options: { ... }, ... ]
  plugins: [
    // { name: 'onehost', options: { host: 'localhost.cnodejs.org' } },
    // { name: 'wordpress_redirect', options: {} }
  ]
};
