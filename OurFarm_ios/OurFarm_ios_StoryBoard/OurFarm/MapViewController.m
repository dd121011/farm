//
//  MapViewController.m
//  OurFarm
//
//  Created by tian hao on 13-6-18.
//  Copyright (c) 2013年 tian hao. All rights reserved.
//

#import "MapViewController.h"

@interface MapViewController () {
    @private
    DestinationAnnotation *navUseAnnotation;//TODO 点击红图钉时,赋值给该变量,供导航时候传值用，没有找到好方法。。。
}

@end

@implementation MapViewController

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
    [super viewDidLoad];
	// Do any additional setup after loading the view.
    locationManager = [[CLLocationManager alloc] init];
    [locationManager setDelegate:self];
    locationManager.distanceFilter = 1000;
    locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    [locationManager startUpdatingLocation];
    
    self.mapView.delegate = self;
	// 是否显示当前位置
    self.mapView.showsUserLocation = YES;
    //标准视图
    mapView.mapType = MKMapTypeStandard;
}

- (void)loadView
{
    [super loadView];
	// Do any additional setup after loading the view.
    //NSLog(@"summaryCount: %d", [self.summaryArray count]);
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark 定位

/*
 * Get location error
 */
- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error
{
    NSLog(@"定位出错: %@", error);
}

/*
 * Get location success
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation
{
    [manager stopUpdatingLocation];
    
    NSLog(@"%f,%f",newLocation.coordinate.latitude, newLocation.coordinate.longitude);
    coordinate.latitude = newLocation.coordinate.latitude;
    coordinate.longitude = newLocation.coordinate.longitude;
    
    //设置显示范围
    MKCoordinateRegion region = MKCoordinateRegionMakeWithDistance(coordinate,50000 ,50000);
    [self.mapView setRegion:region animated:TRUE];
    
    //show annotations
    [self showSummaryAnnotation];
}

#pragma mark 设置景点标记

- (void)locationAddressWithLocation:(CLLocation *)locationGps
                        withSummary:(Summary *) summary
{
    CLGeocoder *clGeoCoder = [[CLGeocoder alloc] init];
    geoCoder = clGeoCoder;
    
    [geoCoder reverseGeocodeLocation:locationGps completionHandler:^(NSArray *placemarks, NSError *error)
     {
         NSLog(@"error %@ placemarks count %d", error.localizedDescription, placemarks.count);
         NSLog(@"%@", placemarks);
         for (CLPlacemark *placeMark in placemarks)
         {
             DestinationAnnotation *annotation = [[DestinationAnnotation alloc] initWithCoords:locationGps.coordinate];
             [annotation setPlaceMark:placeMark];
             [annotation setTitle:[NSString stringWithFormat:@"%@", summary.name]];
             [annotation setSubtitle:summary.address];
             [annotation setTag:summary.destinationId];
             [annotation setLat:[summary.lat doubleValue]];
             [annotation setLng:[summary.lng doubleValue]];
             
             NSLog(@"des value: %d", [summary.destinationId intValue]);
                          
             [self.mapView addAnnotation:annotation];
         }
     }];
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView
            viewForAnnotation:(id <MKAnnotation>)annotation
{
    // 如果是显示当前地点，返回nil
    if ([annotation isKindOfClass:[MKUserLocation class]])
        return nil;
    
    // 如果注解是自定义注解
    if ([annotation isKindOfClass:[DestinationAnnotation class]])
    {
        // 尝试从队列中移出存在的pinView
        MKPinAnnotationView *pinView = (MKPinAnnotationView*)[self.mapView dequeueReusableAnnotationViewWithIdentifier:@"DestinationAnnotation"];
        
        if (!pinView)
        {
            pinView = [[MKPinAnnotationView alloc] initWithAnnotation:annotation reuseIdentifier:@"DestinationAnnotation"];
            pinView.canShowCallout = YES;
            pinView.animatesDrop = YES;
            
            UIButton* rightButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
            [rightButton addTarget:self
                            action:@selector(showDetails:)
                  forControlEvents:UIControlEventTouchUpInside];
            pinView.rightCalloutAccessoryView = rightButton;
            //UIImageView *headImage = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"flag.png"]];
            //pinView.leftCalloutAccessoryView = headImage;
            pinView.opaque = YES;
        }
        else
        {
            pinView.annotation = annotation;
        }
        
        return pinView;
    }
    
    return nil;
}

/*
 *设置地图上的标记，暂时最多只设置十个
 */
-(void) showSummaryAnnotation {
    int size = [self.summaryArray count] > 10 ? 10: [self.summaryArray count];
    
    for (int i = 0; i < size; i++) {
        Summary *summary = [self.summaryArray objectAtIndex:i];
        CLLocation *location = [[CLLocation alloc] initWithLatitude:[summary.lat doubleValue]
                                                          longitude:[summary.lng doubleValue]];
        
        [self locationAddressWithLocation:location
                              withSummary:summary];
    }
    
}

/*
 *点击红图钉时调用
 */
- (void) mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view{
    navUseAnnotation = (DestinationAnnotation *)view.annotation;
}

- (void)showDetails:(id)sender
{
    NSLog(@"subtitle: %@", [navUseAnnotation title]);
    NSLog(@"desId: %@", [navUseAnnotation tag]);
    
    [self performSegueWithIdentifier:@"detailView" sender:[navUseAnnotation tag] ];
}

/*
 * Goto Next Page
 */
-(void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    //进入到详细页面
    if ([segue.identifier isEqualToString:@"detailView"]) {
        DetailViewController *detailView = [segue destinationViewController];
        [detailView setValue:sender forKey:@"destinationId"];
        NSLog(@"detail:%@", sender);
    }
    
}

@end
