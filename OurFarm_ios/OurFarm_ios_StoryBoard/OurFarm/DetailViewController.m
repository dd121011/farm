//
//  DetailViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-7.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "DetailViewController.h"
#import "FarmHomeDB.h"

#import "UIImageView+AFNetworking.h"
#import "CMActionSheet.h"
#import "TSPopoverController.h"
#import "TSActionSheet.h"
#import "SVProgressHUD.h"
#import "AMRatingControl.h"
#import "ShareViewController.h"

@interface DetailViewController ()

/**
 * Loads the detail
 *
 * @private
 */
- (void)loadDetail;
- (void)showActionSheet;
- (void)showMoreAroundFarmTop3:(NSArray *)moreAroundFarmArray;
- (void)showPicture:(UIButton *)farm picUrl:(NSString *) url selectDestinationId:(NSNumber *) destinationId;

@end

@implementation DetailViewController{
    
}
@synthesize destinationId;
@synthesize destination;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [SVProgressHUD show];
    
    //self.navigationController.navigationBar.tintColor = [UIColor greenColor];
    
    [super viewDidLoad];
    
    self.view.contentMode = UIViewContentModeScaleAspectFill;
    
    [self loadDetail];
    
    //[self showAroundFarm];
    /*
            dispatch_queue_t mainQueue = dispatch_get_main_queue();
            dispatch_async(mainQueue, ^(void) {
                [self loadDetail];
                //[self showAroundFarm];
                //[[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:@"123" delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
    
            });
    
    dispatch_async(mainQueue, ^(void) {

        	[self showAroundFarm];
        
    });*/
    
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    return YES;
}

#pragma mark Aux view methods

/*
 * Loads the detail
 */
- (void)loadDetail{
    NSLog(@"desId: %@", self.destinationId);
    
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           self.destinationId, @"destinationId",
                           nil];
    
    [Destination getDetailWithBlock:^(Destination *destination, NSDictionary *json, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get detail error: %@", error);
        } else {
            self.destination = destination;
            self.json = json;
            [self showDetail:destination];
        }
    }
    requestParameter:param];
    
}

/**
 * 展示详细页面
 */
-(void) showDetail:(Destination *)destination {
    Summary * summary = destination.summary;
    //获取经纬度
    self.lat = summary.lat;
    self.lng = summary.lng;
    //pic
    [self.headView setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[destination valueForKeyPath:@"summary.pic"] ]]
                          placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                   success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                       self.headView.image = image;
                                   }
                                   failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                       NSLog(@"Get picture fail in detail: %@", error);
                                   }];
    //设置详细页面内容
    self.name.text = summary.name;//[destination valueForKeyPath:@"summary.name"];
    self.priceInfo.text = summary.priceInfo;
    self.characteristic.text = summary.characteristic;
    [self.address setTitle:summary.address forState:UIControlStateNormal];
    [self.tel setTitle:summary.tel forState:UIControlStateNormal];
    self.details.text = destination.introduction;
    self.promotion.text = destination.preferentialInfo;
    self.detailName.text = summary.name;
    
    
    //评分
    AMRatingControl *simpleRatingControl = [[AMRatingControl alloc] initWithLocation:CGPointMake(0, 0)
                                                                          emptyColor:[UIColor grayColor]
                                                                          solidColor:[UIColor greenColor]
                                                                        andMaxRating:5];
    //set star size
    simpleRatingControl.kFontSize = 12;
    //can not change value
    simpleRatingControl.isSelect = FALSE;
    
    // Customize the current rating
    [simpleRatingControl setRating: [summary.score intValue]];
    [self.score addSubview:simpleRatingControl];
    
    //显示周边农家院或者评论
    if([self.nearbyType intValue] == 1) {
        [self showAroundFarm];
//        dispatch_queue_t mainQueue = dispatch_get_main_queue();
//        dispatch_async(mainQueue, ^(void) {
//            //[self showAroundFarm];
//            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:@"123" delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
//            
//        });
    } else {
        // comment
    }
    
    [SVProgressHUD dismiss];
}

/**
 * 周边农家院
 */
- (void) showAroundFarm {
    //请求参数
    NSDictionary *param = [NSDictionary dictionaryWithObjectsAndKeys:
                           self.destinationId, @"view_id",
                           [NSNumber numberWithInt:1], @"count",
                           nil];
        
    [MoreAroundFarm getMoreAroundListWithBlock:^(NSArray *moreAroundFarmArray, NSError *error) {
        if (error) {
            [[[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error", nil) message:[error localizedDescription] delegate:nil cancelButtonTitle:nil otherButtonTitles:NSLocalizedString(@"OK", nil), nil] show];
            NSLog(@"Get MoreAroundFarm error: %@", error);
            
            self.getAroundFarmMsg.text = @"获取周边农家乐失败。";
            self.getAroundFarmMsg.hidden = NO;
            self.retryGetAroundFarm.hidden = NO;
            self.moreFarmHome.hidden = TRUE;
        } else {
            [self showMoreAroundFarmTop3:moreAroundFarmArray];
        }
    }
                              requestParameter:param];
}

/**
 * 周边农家院图片显示
 */
- (void)showMoreAroundFarmTop3:(NSArray *)moreAroundFarmArray {
    //没有找到农家乐时
    if(moreAroundFarmArray == nil || [moreAroundFarmArray count] <=0) {
        //TODO 隐藏图片
        self.getAroundFarmMsg.text = @"很抱歉，该景点附近没有农家乐。";
        self.getAroundFarmMsg.hidden = NO;
        self.moreFarmHome.hidden = TRUE;
        return;
    } else if ([moreAroundFarmArray count] < 4) {//农家乐条数小于4时，不显示更多按钮
        self.moreFarmHome.hidden = TRUE;
    }
    
    self.getAroundFarmMsg.hidden = TRUE;
    self.retryGetAroundFarm.hidden = TRUE;
    
    for(int i = 0; i < [moreAroundFarmArray count]; i++) {
        if(i == 3) break;

        MoreAroundFarm *maf = [moreAroundFarmArray objectAtIndex:i];        
        if(i == 0) {
            [self showFarmPicture:self.aroundFarm1 label:self.aroundFarmLabel1 aroundFarm:maf];
        } else if(i == 1){
            [self showFarmPicture:self.aroundFarm2 label:self.aroundFarmLabel2 aroundFarm:maf];
        } else if(i == 2){
            [self showFarmPicture:self.aroundFarm3 label:self.aroundFarmLabel3 aroundFarm:maf];
        }
    }

    
}
/**
 * 点击周边农家乐
 */
-(void)showFarmPicture:(UIImageView *)aroundFarm
                 label:(UILabel *)aroundFarmLabel
            aroundFarm:(MoreAroundFarm *)maf {
    Summary *summary = maf.summary;
    UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector (clickAroundFarm:)];
    [self showPicture:aroundFarm picUrl:summary.pic selectDestinationId:maf.farmhome_id];
    [aroundFarmLabel setText: summary.name];
    aroundFarm.userInteractionEnabled = YES;
    [aroundFarm addGestureRecognizer:tap];
    
    aroundFarm.hidden = NO;
    aroundFarmLabel.hidden = NO;
}

/**
 * 点击周边农家乐
 */
-(void) clickAroundFarm: (UITapGestureRecognizer *) tap{
    UIView *view = [tap view];
    NSLog(@"qwe%@", [NSNumber numberWithInt: [view tag]]);
    
    //DetailViewController *detailVC = [[DetailViewController alloc] init];
    //detailVC.destinationId = [NSNumber numberWithInt: [view tag]];
    //self.destinationId = [NSNumber numberWithInt: [view tag]];
    //[self loadDetail];
    
    [self performSegueWithIdentifier:@"detailFarmHome" sender:[NSNumber numberWithInt: [view tag]]];
}

/**
 *显示图片
 */
-(void) showPicture:(UIImageView *)farm
             picUrl:(NSString *) url
selectDestinationId:(NSNumber *) destinationId{
    //__weak UIImageView *iv = [[UIImageView alloc] init];
    [farm setImageWithURLRequest:[[NSURLRequest alloc] initWithURL:[NSURL URLWithString:url]]
                         placeholderImage:[UIImage imageNamed:@"home_placeholder_pic"]
                                  success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image){
                                      farm.image = image;
                                      farm.tag = [destinationId intValue];
                                      NSLog(@"farm.tag:%d", [destinationId intValue]);
                                  }
                                  failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error){
                                      NSLog(@"Get picture fail in detail: %@", error);
                                  }];
}

-(void) showActionSheet:(id)sender forEvent:(UIEvent*)event
{
    TSActionSheet *actionSheet = [[TSActionSheet alloc] initWithTitle:@"action sheet"];
    [actionSheet destructiveButtonWithTitle:@"hoge" block:nil];
    [actionSheet addButtonWithTitle:@"hoge1" block:^{
        NSLog(@"pushed hoge1 button");
    }];
    [actionSheet addButtonWithTitle:@"moge2" block:^{
        NSLog(@"pushed hoge2 button");
    }];
    [actionSheet cancelButtonWithTitle:@"Cancel" block:nil];
    actionSheet.cornerRadius = 5;
    
    [actionSheet showWithTouch:event];
}

/**
 * 打开苹果自身地图应用或者使用google地图显示路线
 */
- (void)showLineOnMap
{    
    //TODO test line 当前位置如何确认？
    CLLocationCoordinate2D coords1 = CLLocationCoordinate2DMake(30.691793,104.088264);
    CLLocationCoordinate2D coords2 = CLLocationCoordinate2DMake(30.691293,104.088264);
    if (SYSTEM_VERSION_LESS_THAN(@"6.0"))// ios6以下，调用google map
    {
        NSString *urlString = [[NSString alloc]
                               initWithFormat:@"http://maps.google.com/maps?saddr=%f,%f&daddr=%f,%f&dirfl=d",
                               coords1.latitude,coords1.longitude,coords2.latitude,coords2.longitude];
        NSURL *aURL = [NSURL URLWithString:urlString];
        //打开网页google地图
        [[UIApplication sharedApplication] openURL:aURL];
    }else// 直接调用ios自己带的apple map
    {    /*
          //keys
          MKLaunchOptionsMapCenterKey:地图中心的坐标(NSValue)
          MKLaunchOptionsMapSpanKey:地图显示的范围(NSValue)
          MKLaunchOptionsShowsTrafficKey:是否显示交通信息(boolean NSNumber)
          
          //MKLaunchOptionsDirectionsModeKey: 导航类型(NSString)
          {
          MKLaunchOptionsDirectionsModeDriving:驾车
          MKLaunchOptionsDirectionsModeWalking:步行
          }
          
          //MKLaunchOptionsMapTypeKey:地图类型(NSNumber)
          enum {
          MKMapTypeStandard = 0,
          MKMapTypeSatellite,
          MKMapTypeHybrid
          };
          */
        
        NSLog(@"lat: %@", self.lat);
        NSLog(@"lng: %@", self.lng);
        
        //打开苹果自身地图应用，并呈现特定的item     
        CLLocationCoordinate2D destinationLocation;
        //要去的目标经纬度
        destinationLocation.latitude = [self.lat doubleValue];
        destinationLocation.longitude = [self.lng doubleValue];
        MKMapItem *currentLocation = [MKMapItem mapItemForCurrentLocation];//调用自带地图（定位）
        //显示目的地坐标。画路线
        MKMapItem *toLocation = [[MKMapItem alloc] initWithPlacemark:[[MKPlacemark alloc] initWithCoordinate:destinationLocation addressDictionary:nil]];
        toLocation.name = self.name.text;
        [MKMapItem openMapsWithItems:[NSArray arrayWithObjects:currentLocation, toLocation, nil]
                       launchOptions:[NSDictionary dictionaryWithObjects:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeDriving, [NSNumber numberWithBool:YES], nil]
                                      
                                                                 forKeys:[NSArray arrayWithObjects:MKLaunchOptionsDirectionsModeKey, MKLaunchOptionsShowsTrafficKey, nil]]];
    }
    
}

// 在这里处理UIAlertView中的按钮被单击的事件
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    NSLog(@"buttonIndex is : %i",buttonIndex);
    switch (buttonIndex) {
        case 0:{//取消
            NSLog(@"cancle");
        }
        break;
        case 1:{//拨打电话
            [[UIApplication sharedApplication] openURL:
                [NSURL URLWithString:[NSString stringWithFormat:@"tel: %@", [[self.tel titleLabel] text]]]];
        }
        break;

        default:
            break;
    }
}

- (IBAction)getMoreFarmHome:(id)sender {
}

- (IBAction)callPhone:(id)sender{
    NSLog(@"Dismiss action sheet with \"phone\"");
    //[self showActionSheet:sender forEvent:nil];
    
    [[[UIAlertView alloc] initWithTitle:@"拨打电话" message:[[self.tel titleLabel] text] delegate:self cancelButtonTitle:@"取消" otherButtonTitles:@"确定", nil] show];
}

- (IBAction)showAddressOnMap:(id)sender {
    NSLog(@"Dismiss action sheet with \"map\"");
    [self showLineOnMap];
}

/**
 * 重试按钮
 */
- (IBAction)retryGetFarmHome:(id)sender {
    [self showAroundFarm];
}

- (IBAction)addFavorite:(id)sender {
    BOOL addSucess = [FarmHomeDB addFavorite: [self.destinationId intValue]];
    if(addSucess == FALSE) {
        [SVProgressHUD showErrorWithStatus:@"收藏失败"];
        return;
    }
        
    //查看本地有没有存储该景点
    if([FarmHomeDB findDestinationById: [NSString stringWithFormat:@"%@", self.destinationId ]] == nil) {
        NSLog(@"select Destination");
        [FarmHomeDB addDestination:self.destination
                   destinationJSON:self.json];
    }
    
    [SVProgressHUD showSuccessWithStatus:@"收藏成功"];
}

/**
 *添加到行程
 */
- (IBAction)addTrip:(id)sender{
    ThisTrip *thisTrip = [FarmHomeDB findThisTripById: [NSString stringWithFormat:@"%@", self.destinationId]];
    if(thisTrip != nil) {
        [SVProgressHUD showSuccessWithStatus:@"添加到行程成功"];
        return;
    }
    
    BOOL addSucess = [FarmHomeDB addThisTrip:[self.destinationId intValue]];
    if(addSucess == FALSE) {
        [SVProgressHUD showErrorWithStatus:@"添加到行程失败"];
    }
    
    //查看本地有没有存储该景点
    if([FarmHomeDB findDestinationById: [NSString stringWithFormat:@"%@", self.destinationId ]] == nil) {
        NSLog(@"select Destination");
        [FarmHomeDB addDestination:self.destination
                   destinationJSON:self.json];
    }
    
    [SVProgressHUD showSuccessWithStatus:@"添加到行程成功"];
}

//分享
- (IBAction)share:(id)sender {
    Destination *destination = self.destination;
    Summary *summary = destination.summary;
    NSString *weiboContent = @"我在郊游客发现一个好地方：";
    
    weiboContent = [weiboContent stringByAppendingString: summary.name];
    
    NSLog(@"weiboContent: %@", weiboContent);
    [self performSegueWithIdentifier:@"shareView" sender:weiboContent];
}

#pragma mark segue
/*
 * Goto Next Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    NSLog(@"segue");
    UINavigationController *navigationController = [segue destinationViewController];
    //进入到详细页面
    if ([segue.identifier isEqualToString:@"detailFarmHome"]) {
        
        DetailViewController *detailView = [navigationController viewControllers][0];
        if ([detailView respondsToSelector:@selector(setDestinationId:)]) {
            [detailView setValue:sender forKey:@"destinationId"];
        }
        if ([detailView respondsToSelector:@selector(setNearbyType:)]) {
            [detailView setValue:self.nearbyType forKey:@"nearbyType"];
        }
    }
    //returnDetailView
    if ([segue.identifier isEqualToString:@"returnDetailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        if ([detailView respondsToSelector:@selector(setDestinationId:)]) {
            [detailView setValue:[NSNumber numberWithInt:2] forKey:@"destinationId"];
        }
    }
    
    //进入到share页面
    if ([segue.identifier isEqualToString:@"shareView"]) {        
        ShareViewController *shareView = [segue destinationViewController];
        NSLog(@"sender: %@", sender);
        if ([shareView respondsToSelector:@selector(setShareText:)]) {
            [shareView setValue:sender forKey:@"shareText"];
        }
    }
}


@end
