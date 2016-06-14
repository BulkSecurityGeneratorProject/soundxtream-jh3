'use strict';

describe('Controller Tests', function() {

    describe('User_activity Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockUser_activity, MockUser, MockTrack, MockPlaylist;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockUser_activity = jasmine.createSpy('MockUser_activity');
            MockUser = jasmine.createSpy('MockUser');
            MockTrack = jasmine.createSpy('MockTrack');
            MockPlaylist = jasmine.createSpy('MockPlaylist');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'User_activity': MockUser_activity,
                'User': MockUser,
                'Track': MockTrack,
                'Playlist': MockPlaylist
            };
            createController = function() {
                $injector.get('$controller')("User_activityDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'soundxtream3App:user_activityUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
